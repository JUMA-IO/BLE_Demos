//
//  JumaOtaModeSetter.m
//  OTA_Tool
//
//  Created by 汪安军 on 16/4/22.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import "JumaOtaModeSetter.h"
#import "JumaDevice+OtaModeExtension.h"

@interface JumaOtaModeSetter () <JumaManagerDelegate, JumaDeviceDelegate>

@property (nonatomic, copy) NSString *targetDeviceUUID;

@property (nonatomic, strong) JumaManager *manager;
@property (nonatomic, strong) JumaDevice *device;

/// 连接设备失败后, 最大重试次数
@property (nonatomic) NSUInteger maxAllowedRetryCountWhenFailToConnectDevice;
/// 连接失败后, 已经重试的次数
@property (nonatomic) NSUInteger retryedCount;

@property (nonatomic) BOOL didSendOtaModeData;
@property (nonatomic) BOOL disconnectionBecauseSetOtaMode;

@end

@implementation JumaOtaModeSetter

- (instancetype)initWithDelegate:(nullable id<JumaOtaModeSetterDelegate>)delegate targetDeviceUUID:(NSString *)UUID {
    NSParameterAssert(UUID != nil);
    
    self = [super init];
    if (self) {
        _delegate = delegate;
        _targetDeviceUUID = [UUID copy];
    }
    return self;
}

#pragma mark - Public

- (void)setWithMaxAllowedRetryCountWhenFailToConnectDevice:(NSUInteger)retryCount {
    self.maxAllowedRetryCountWhenFailToConnectDevice = retryCount;
    self.retryedCount = 0;
    self.didSendOtaModeData = NO;
    
    self.manager = [[JumaManager alloc] initWithDelegate:self queue:nil options:nil];
}

- (void)disconnectDevice {
    if (self.device && self.device.state != JumaDeviceStateDisconnected) {
        self.manager.delegate = nil;
        self.device.delegate = nil;
        [self.manager disconnectDevice:self.device];
    }
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
    [self.delegate connectionIsNotDisconnectedAsExpectedAfterSendOtaModeData:self];
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
    [self.delegate otaModeSetterDidDiscoverTargetDevice:self];
    
    // 第一次扫描到设备
    if (!self.didSendOtaModeData) {
        
        if (device.isInOtaMode) {
            self.manager = nil;
            [self.delegate otaModeSetter:self didSetOtaMode:YES];
        } else {
            device.delegate = self;
            self.device = device;
            [manager connectDevice:device];
        }
    }
    // 发送 "OTA_Mode" 之后扫描到设备
    else {
        self.manager = nil;
        [self.delegate otaModeSetter:self didSetOtaMode:device.isInOtaMode];
    }
}

- (void)manager:(JumaManager *)manager didConnectDevice:(JumaDevice *)device {
    [self.delegate otaModeSetterDidConnectTargetDevice:self];
    [device setOtaMode];
}

- (void)manager:(JumaManager *)manager didDisconnectDevice:(JumaDevice *)device error:(NSError *)error {
    self.device = nil;
    
    if (self.disconnectionBecauseSetOtaMode) {
        self.disconnectionBecauseSetOtaMode = NO;
        
        [self cancelDisconnectionAndNotification];
        [manager scanForDeviceWithOptions:nil];
//        [self.logger saveOneLine:@"连接断开, %@", error.localizedDescription, nil];
//        [self.logger saveOneLine:@"忽略连接断开一次", nil];
//        [self.logger saveOneLine:@"开启扫描, 检查 OTA 是否设置成功", nil];
    } else {
        [self.delegate otaModeSetter:self didDisconnectTargetDevice:error];
    }
}

- (void)manager:(JumaManager *)manager didFailToConnectDevice:(JumaDevice *)device error:(NSError *)error {
    
    if (self.retryedCount < self.maxAllowedRetryCountWhenFailToConnectDevice) {
        self.retryedCount++;
        NSLog(@"连接失败, %@", error.localizedDescription);
        [manager connectDevice:device];
    } else {
        [self.delegate otaModeSetter:self didFailToConnectTargetDevice:error];
    }
}

#pragma mark - <JumaDeviceDelegate>

- (void)device:(JumaDevice *)device didWriteData:(NSError *)error {
    [self.delegate otaModeSetter:self didSendOtaModeData:error];
    
    if (!error) {
        self.didSendOtaModeData = YES;
        self.disconnectionBecauseSetOtaMode = YES;
        
        // 检查发送 "OTA_Mode" 后, 连接是否能够自动断开
        [self performDisconnectionAndNotificaionAfterDelay:5];
    }
}

@end
