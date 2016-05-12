//
//  JumaFirmwareUpdater.h
//  OTA_Tool
//
//  Created by 汪安军 on 16/4/20.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import <Foundation/Foundation.h>
@protocol JumaFirmwareUpdaterDelegate;

NS_ASSUME_NONNULL_BEGIN

@interface JumaFirmwareUpdater : NSObject

@property (nonatomic, weak) id<JumaFirmwareUpdaterDelegate> delegate;

@property (nonatomic, readonly, copy) NSString *targetDeviceUUID;

- (instancetype)init __attribute__((unavailable("use -initWithDelegate:targetDeviceUUID: instead")));
- (instancetype)initWithDelegate:(nullable id<JumaFirmwareUpdaterDelegate>)delegate targetDeviceUUID:(NSString *)UUID  NS_DESIGNATED_INITIALIZER;

- (void)updateWithData:(NSData *)data maxAllowedRetryCountWhenFailToConnectDevice:(NSUInteger)retryCount;

@end

@protocol JumaFirmwareUpdaterDelegate <NSObject>

@optional

- (void)firmwareUpdaterDidDiscoverTargetDevice:(JumaFirmwareUpdater *)firmwareUpdater;
- (void)firmwareUpdaterDidConnectTargetDevice:(JumaFirmwareUpdater *)firmwareUpdater;

@required

- (void)firmwareUpdater:(JumaFirmwareUpdater *)firmwareUpdater didFailToConnectTargetDevice:(NSError *)error;
- (void)firmwareUpdater:(JumaFirmwareUpdater *)firmwareUpdater didDisconnectTargetDevice:(NSError *)error;

- (void)firmwareUpdater:(JumaFirmwareUpdater *)firmwareUpdater didSendFirmwareData:(nullable NSError *)error;
- (void)connectionIsNotDisconnectedAsExpectedAfterSendFirmwareData:(JumaFirmwareUpdater *)firmwareUpdater;

- (void)firmwareUpdater:(JumaFirmwareUpdater *)firmwareUpdater didUpdateFirmware:(BOOL)success;

@end

NS_ASSUME_NONNULL_END
