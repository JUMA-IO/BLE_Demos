//
//  JumaOtaModeSetter.h
//  OTA_Tool
//
//  Created by 汪安军 on 16/4/22.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import <Foundation/Foundation.h>
@protocol JumaOtaModeSetterDelegate;

NS_ASSUME_NONNULL_BEGIN

@interface JumaOtaModeSetter : NSObject

@property (nonatomic, readonly, copy) NSString *targetDeviceUUID;

@property (nonatomic, weak) id<JumaOtaModeSetterDelegate> delegate;

- (instancetype)init __attribute__((unavailable("use -initWithDelegate:targetDeviceUUID: instead")));
// 1 必须先指定要更新固件的那个设备
- (instancetype)initWithDelegate:(nullable id<JumaOtaModeSetterDelegate>)delegate targetDeviceUUID:(NSString *)UUID NS_DESIGNATED_INITIALIZER;

// 2
- (void)setWithMaxAllowedRetryCountWhenFailToConnectDevice:(NSUInteger)retryCount;

@end

@protocol JumaOtaModeSetterDelegate <NSObject>

@optional

// 3
// 7
- (void)otaModeSetterDidDiscoverTargetDevice:(JumaOtaModeSetter *)otaModeSetter;
// 4
- (void)otaModeSetterDidConnectTargetDevice:(JumaOtaModeSetter *)otaModeSetter;

@required

- (void)otaModeSetter:(JumaOtaModeSetter *)otaModeSetter didFailToConnectTargetDevice:(NSError *)error;
- (void)otaModeSetter:(JumaOtaModeSetter *)otaModeSetter didDisconnectTargetDevice:(NSError *)error;

// 5 让设备重启并运行在 OTA 模式下 (设备只有在 OTA 模式下才能接受并处理新版固件)
- (void)otaModeSetter:(JumaOtaModeSetter *)otaModeSetter didSendOtaModeData:(nullable NSError *)error;
// 6 如果设备支持 OTA 模式, 在手机端设置 OTA 模式后, 二者的连接会由设备自动断开
//   如果设置 OTA 模式后 5s 内, 连接没有断开, 这个方法就会被调用
- (void)connectionIsNotDisconnectedAsExpectedAfterSendOtaModeData:(JumaOtaModeSetter *)otaModeSetter;
// 8 已经检查设备是否运行在 OTA 模式下
- (void)otaModeSetter:(JumaOtaModeSetter *)otaModeSetter didSetOtaMode:(BOOL)success;

@end

NS_ASSUME_NONNULL_END
