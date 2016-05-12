//
//  JumaFirmwareUpdater.m
//  OTA_Tool
//
//  Created by 汪安军 on 16/4/20.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import "JumaFirmwareUpdater.h"
#import "JumaDevice+OtaModeExtension.h"

@interface JumaFirmwareUpdater () <JumaManagerDelegate, JumaDeviceDelegate>

@property (nonatomic, copy) NSString *targetDeviceUUID;

@property (nonatomic, strong) JumaManager *manager;
@property (nonatomic, strong) JumaDevice *device;

@property (nonatomic) BOOL didSendFirmwareData;

@property (nonatomic, copy) NSData *firmwareData;

@property (nonatomic) BOOL disconnectionBecauseUpdate;

/// 连接设备失败后, 最大重试次数
@property (nonatomic) NSUInteger maxAllowedRetryCountWhenFailToConnectDevice;
/// 连接失败后, 已经重试的次数
@property (nonatomic) NSUInteger retryedCount;

@end

@implementation JumaFirmwareUpdater

- (instancetype)initWithDelegate:(nullable id<JumaFirmwareUpdaterDelegate>)delegate targetDeviceUUID:(NSString *)UUID {
    NSParameterAssert(UUID != nil);
    
    self = [super init];
    if (self) {
        _delegate = delegate;
        _targetDeviceUUID = [UUID copy];
    }
    return self;
}

#pragma mark - Public

- (void)updateWithData:(NSData *)data maxAllowedRetryCountWhenFailToConnectDevice:(NSUInteger)retryCount {
    self.maxAllowedRetryCountWhenFailToConnectDevice = retryCount;
    self.retryedCount = 0;
    
    self.firmwareData = data;
    self.didSendFirmwareData = NO;
    
    self.manager = [[JumaManager alloc] initWithDelegate:self queue:nil options:nil];
}

#pragma mark - Private

- (void)performDisconnectionAndNotificaionAfterDelay:(NSTimeInterval)delay {
    SEL selector = @selector(disconnectDeviceAndNotifyDelegateConnectionWasNotDisconnected);
    [self performSelector:selector withObject:nil afterDelay:delay];
}

- (void)cancelDisconnectionAndNotification {
    SEL selector = @selector(disconnectDeviceAndNotifyDelegateConnectionWasNotDisconnected);
    [NSObject cancelPreviousPerformRequestsWithTarget:self selector:selector object:nil];
}

- (void)disconnectDeviceAndNotifyDelegateConnectionWasNotDisconnected {
    // disconnect device
    self.manager.delegate = nil;
    self.device.delegate = nil;
    [self.manager disconnectDevice:self.device];
    
    // notify delegate
    [self.delegate connectionIsNotDisconnectedAsExpectedAfterSendFirmwareData:self];
}

#pragma mark - <JumaManagerDelegate>

- (void)managerDidUpdateState:(JumaManager *)manager {
    
    if (manager.state == JumaManagerStatePoweredOn) {
        [manager scanForDeviceWithOptions:nil];
    } else {
        self.manager = nil;
        self.device = nil;
    }
}

- (void)manager:(JumaManager *)manager didDiscoverDevice:(JumaDevice *)device RSSI:(NSNumber *)RSSI {
    if (![device.UUID isEqualToString:self.targetDeviceUUID]) { return; }
    
    [manager stopScan];
    [self.delegate firmwareUpdaterDidDiscoverTargetDevice:self];
    
    // 第一次扫描到设备
    if (!self.didSendFirmwareData) {
        
        if (device.isInOtaMode) {
            device.delegate = self;
            self.device = device;
            [manager connectDevice:device];
        } else {
            self.manager = nil;
            self.firmwareData = nil;
            [self.delegate firmwareUpdater:self didUpdateFirmware:YES];
        }
    }
    // 发送固件之后扫描到设备
    else {
        self.manager = nil;
        self.firmwareData = nil;
        [self.delegate firmwareUpdater:self didUpdateFirmware:!device.isInOtaMode];
    }
}

- (void)manager:(JumaManager *)manager didConnectDevice:(JumaDevice *)device {
    [self.delegate firmwareUpdaterDidConnectTargetDevice:self];
    [device updateFirmware:self.firmwareData];
}

- (void)manager:(JumaManager *)manager didDisconnectDevice:(JumaDevice *)device error:(NSError *)error {
    self.device = nil;
    
    if (self.disconnectionBecauseUpdate) {
        // 发送固件后, 蓝牙设备会主动断开连接, 这次断开是应该发生的, 应该忽略它
        self.disconnectionBecauseUpdate = NO;
        [self cancelDisconnectionAndNotification];
        
        // 开启扫描, 检查固件是否更新成功
        [manager scanForDeviceWithOptions:nil];
    } else {
        [self.delegate firmwareUpdater:self didDisconnectTargetDevice:error];
    }
}

- (void)manager:(JumaManager *)manager didFailToConnectDevice:(JumaDevice *)device error:(NSError *)error {
    
    if (self.retryedCount < self.maxAllowedRetryCountWhenFailToConnectDevice) {
        self.retryedCount++;
        NSLog(@"连接失败, %@", error.localizedDescription);
        [manager connectDevice:device];
    } else {
        [self.delegate firmwareUpdater:self didFailToConnectTargetDevice:error];
    }
}

#pragma mark - <JumaDeviceDelegate>

- (void)device:(JumaDevice *)device didUpdateFirmware:(NSError *)error {
    [self.delegate firmwareUpdater:self didSendFirmwareData:error];
    
    if (!error) {
        self.didSendFirmwareData = YES;
        self.disconnectionBecauseUpdate = YES;
        
        // 发送固件后, 检查连接是否能够自动断开
        [self performDisconnectionAndNotificaionAfterDelay:5];
    }
}

@end
