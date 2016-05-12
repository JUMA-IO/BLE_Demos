//
//  ViewController.m
//  OTA_Demo_iOS
//
//  Created by 汪安军 on 16/5/12.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import "ViewController.h"
#import "DevicesTableViewController.h"
#import "UIAlertView+JUMAExtension.h"
#import "NSTimer+Block.h"

@import JumaBluetoothSDK;
#import "JumaDevice+OtaModeExtension.h"
#import "JumaOtaModeSetter.h"
#import "JumaFirmwareUpdater.h"

@interface ViewController () <JumaManagerDelegate, JumaOtaModeSetterDelegate, JumaFirmwareUpdaterDelegate>

@property (nonatomic, weak) DevicesTableViewController *devicesTableViewController;

@property (nonatomic, strong) JumaManager *manager;

@property (nonatomic, weak) NSTimer *scanTimer;

@property (nonatomic, strong) JumaOtaModeSetter *otaModeSetter;
@property (nonatomic, strong) JumaFirmwareUpdater *firmwareUpdater;

@property (nonatomic, copy) NSData *firmwareData;

@property (weak, nonatomic) IBOutlet UIBarButtonItem *refreshBarButtonItem;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *updateBarButtonItem;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.manager = [[JumaManager alloc] initWithDelegate:self queue:nil options:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBActions

- (IBAction)refresh:(UIBarButtonItem *)sender {
    [self scan];
    [self.devicesTableViewController refreshDevices];
}

- (IBAction)updateFirmware:(UIBarButtonItem *)sender {
    sender.enabled = NO;
    
    if (!self.firmwareData) {
        NSString *path = [[NSBundle mainBundle] pathForResource:@"app.ota.bin" ofType:nil];
        self.firmwareData = [NSData dataWithContentsOfFile:path];
        NSAssert(self.firmwareData, @"can not read firmware data");
    }
    
    JumaDevice *selectedDevice = self.devicesTableViewController.selectedDevice;
    NSLog(@"------------------------------------------------------------------");
    NSLog(@"选中了设备 %@", selectedDevice);
    
    self.otaModeSetter = [[JumaOtaModeSetter alloc] initWithDelegate:self targetDeviceUUID:selectedDevice.UUID];
    self.firmwareUpdater = [[JumaFirmwareUpdater alloc] initWithDelegate:self targetDeviceUUID:selectedDevice.UUID];
    
    if (selectedDevice.isInOtaMode) {
        [self.firmwareUpdater updateWithData:self.firmwareData maxAllowedRetryCountWhenFailToConnectDevice:10];
    } else {
        [self.otaModeSetter setWithMaxAllowedRetryCountWhenFailToConnectDevice:10];
    }
}

#pragma mark - Private

- (void)scan {
    __weak typeof(self) self_weak = self;
    void (^scanBlock)() = ^() {
        __strong typeof(self) self = self_weak;
        
        [self.manager scanForDeviceWithOptions:nil];
    };
    
    [self.scanTimer invalidate];
    self.scanTimer = [NSTimer juma_scheduledTimerWithTimeInterval:1 repeats:YES block:scanBlock];
    
    //
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        scanBlock();
    });
}

#pragma mark - <JumaManagerDelegate>

- (void)managerDidUpdateState:(JumaManager *)manager {
    
    self.refreshBarButtonItem.enabled = manager.state == JumaManagerStatePoweredOn;
    
    switch (manager.state) {
        case JumaManagerStateUnknown: {
            break;
        }
        case JumaManagerStateResetting: {
            break;
        }
        case JumaManagerStateUnsupported: {
            break;
        }
        case JumaManagerStateUnauthorized: {
            break;
        }
        case JumaManagerStatePoweredOff: {
            [UIAlertView alertWithTitle:@"请打开蓝牙" cancelButtonTitle:@"OK"];
            break;
        }
        case JumaManagerStatePoweredOn: {
            [self scan];
            break;
        }
    }
}

- (void)manager:(JumaManager *)manager didDiscoverDevice:(JumaDevice *)device RSSI:(NSNumber *)RSSI {
    [self.devicesTableViewController updateRSSI:RSSI forDevice:device];
}
#pragma mark - <JumaOtaModeSetterDelegate>

- (void)otaModeSetterDidDiscoverTargetDevice:(JumaOtaModeSetter *)otaModeSetter {
    NSLog(@"发现了目标设备");
}

// 4 连接了设备
- (void)otaModeSetterDidConnectTargetDevice:(JumaOtaModeSetter *)otaModeSetter {
    NSLog(@"连接了目标设备");
}

- (void)otaModeSetter:(JumaOtaModeSetter *)otaModeSetter didFailToConnectTargetDevice:(NSError *)error {
    self.updateBarButtonItem.enabled = YES;
    NSLog(@"连接失败");
}

- (void)otaModeSetter:(JumaOtaModeSetter *)otaModeSetter didDisconnectTargetDevice:(NSError *)error {
    self.updateBarButtonItem.enabled = YES;
    NSLog(@"连接异常断开, %@", error.localizedDescription);
}

- (void)otaModeSetter:(JumaOtaModeSetter *)otaModeSetter didSendOtaModeData:(nullable NSError *)error {
    
    if (error) {
        self.updateBarButtonItem.enabled = YES;
        NSLog(@"发送用来设置 OTA 模式的数据失败, %@", error.localizedDescription);
    } else {
        NSLog(@"发送用来设置 OTA 模式的数据成功");
    }
}

- (void)connectionIsNotDisconnectedAsExpectedAfterSendOtaModeData:(JumaOtaModeSetter *)otaModeSetter {
    self.updateBarButtonItem.enabled = YES;
    NSLog(@"断开连接超时, 设备可能不支持 OTA 模式");
}

// 7 已经检查设备是否运行在 OTA 模式下
- (void)otaModeSetter:(JumaOtaModeSetter *)otaModeSetter didSetOtaMode:(BOOL)success {
    
    if (success) {
        NSLog(@"设置 OTA 模式成功, 即将更新固件");
        [self.firmwareUpdater updateWithData:self.firmwareData maxAllowedRetryCountWhenFailToConnectDevice:10];
    } else {
        self.updateBarButtonItem.enabled = YES;
        NSLog(@"设置 OTA 模式失败");
    }
}

#pragma mark - <JumaFirmwareUpdaterDelegate>

- (void)firmwareUpdaterDidDiscoverTargetDevice:(JumaFirmwareUpdater *)firmwareUpdater {
    NSLog(@"发现了目标设备");
}

// 4 连接了设备
- (void)firmwareUpdaterDidConnectTargetDevice:(JumaFirmwareUpdater *)firmwareUpdater {
    NSLog(@"连接了目标设备");
}

- (void)firmwareUpdater:(JumaFirmwareUpdater *)firmwareUpdater didFailToConnectTargetDevice:(NSError *)error {
    self.updateBarButtonItem.enabled = YES;
    NSLog(@"连接失败");
}

- (void)firmwareUpdater:(JumaFirmwareUpdater *)firmwareUpdater didDisconnectTargetDevice:(NSError *)error {
    self.updateBarButtonItem.enabled = YES;
    NSLog(@"连接异常断开, %@", error.localizedDescription);
}

- (void)firmwareUpdater:(JumaFirmwareUpdater *)firmwareUpdater didSendFirmwareData:(nullable NSError *)error {
    
    if (error) {
        self.updateBarButtonItem.enabled = YES;
        NSLog(@"发送固件数据失败, %@", error.localizedDescription);
    } else {
        NSLog(@"发送固件数据成功");
    }
}

- (void)connectionIsNotDisconnectedAsExpectedAfterSendFirmwareData:(JumaFirmwareUpdater *)firmwareUpdater {
    self.updateBarButtonItem.enabled = YES;
    NSLog(@"断开连接超时, 设备可能不支持更新固件");
}

// 10 结束整个过程, 给出更新操作的结果
- (void)firmwareUpdater:(JumaFirmwareUpdater *)firmwareUpdater didUpdateFirmware:(BOOL)success {
    self.updateBarButtonItem.enabled = YES;
    
    if (success) {
        NSLog(@"更新固件成功");
    } else {
        NSLog(@"更新固件失败");
    }
}

#pragma mark - Override from UIViewController

- (void)addChildViewController:(UIViewController *)childController {
    [super addChildViewController:childController];
    
    if ([childController isKindOfClass:[DevicesTableViewController class]]) {
        self.devicesTableViewController = (id)childController;
    }
}

@end
