//
//  JumaDevice.h
//  JumaBluetoothSDK
//
//  Created by 汪安军 on 15/7/16.
//  Copyright (c) 2015年 JUMA. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void (^JumaReadRssiBlock)(NSNumber *RSSI, NSError *error);
typedef void (^JumaUpdateFirmwareBlock)(NSError *error);

/*!
 *  @enum JumaDeviceState
 *
 *  @discussion Represents the current connection state of a JumaDevice.
 *
 *  @constant JumaDeviceStateDisconnected  The device is currently not connected to the manager.
 *  @constant JumaDeviceStateConnecting    The device is currently in the process of connecting to the manager.
 *  @constant JumaDeviceStateConnected     The device is currently connected to the manager.
 *
 */
typedef NS_ENUM(NSInteger, JumaDeviceState) {
    JumaDeviceStateDisconnected = 0,
    JumaDeviceStateConnecting,
    JumaDeviceStateConnected,
} NS_AVAILABLE(NA, 7_0);

@protocol JumaDeviceDelegate;

NS_CLASS_AVAILABLE(NA, 7_0) @interface JumaDevice : NSObject <NSCopying>


/*!
 *  @property delegate
 *
 *  @discussion The delegate object that will receive device events.
 */
@property(weak, nonatomic) id<JumaDeviceDelegate> delegate;

/*!
 *  @property UUID
 *
 *  @discussion The unique, persistent identifier associated with the device.
 */
@property(copy, nonatomic, readonly) NSString *UUID NS_AVAILABLE(NA, 7_0);

/*!
 *  @property name
 *
 *  @discussion The name of the device.
 */
@property(retain, readonly) NSString *name;

/*!
 *  @property state
 *
 *  @discussion The current connection state of the device.
 */
@property(readonly) JumaDeviceState state;



/*!
 *  @method readRSSI
 *
 *  @discussion While connected, retrieves the current RSSI of the link.
 *
 *  @see        device:didReadRSSI:error:
 */
- (void)readRSSI;
/*!
 *  @method readRSSI:
 *
 *  @param handler A block which receives the results of the reading operation.
 *
 *  @discussion    While connected, retrieves the current RSSI of the link.
 *                 If <i>handler</i> is <i>nil</i>, this method will result in a call to { device:didReadRSSI:error: },
 *                 otherwise, only this block will be called.
 *
 *  @see           device:didReadRSSI:error:
 */
- (void)readRSSI:(JumaReadRssiBlock)handler;


/*!
 *  @method writeData:type:
 *
 *  @param data       The data to be sent. The length of data must be less than 199 and this parameter must not be <i>nil</i>.
 *  @param typeCode   The type of the sent data. This parameter must be less than 128.
 *
 *  @discussion       Sends data to the connected device.
 *
 *  @see              device:didWriteData:
 */
- (void)writeData:(NSData *)data type:(const unsigned char)typeCode;

/*!
 *  @method setOtaMode
 *
 *  @discussion       Sends some data to the connected device, then the device will work in a special mode that allow 
 *                    updating the firmware of the device over the air.
 *
 *  @see              device:didWriteData:
 */
- (void)setOtaMode;


/*!
 *  @method updateFirmware:
 *
 *  @param firmwareData  The firmware to send. This parameter must not be <i>nil</i>.
 *
 *  @discussion          Updates the firmware of the connected device.
 *
 *  @see                 device:didUpdateFirmware:
 */
- (void)updateFirmware:(NSData *)firmwareData;
/*!
 *  @method updateFirmware:completionHandler:
 *
 *  @param firmwareData  The firmware to send. This parameter must not be <i>nil</i>.
 *  @param handler       A block which receives the results of the updating operation.
 *
 *  @discussion          Updates the firmware of the connected device.
 *                       If <i>handler</i> is <i>nil</i>, this method will result in a call to { device:didUpdateFirmware: },
 *                       otherwise, only this block will be called.
 *
 *  @see                 device:didUpdateFirmware:
 */
- (void)updateFirmware:(NSData *)firmwareData completionHandler:(JumaUpdateFirmwareBlock)handler;

@end

#pragma mark - JumaDeviceDelegate

@protocol JumaDeviceDelegate <NSObject>

@optional

/*!
 *  @method device:didReadRSSI:error:
 *
 *  @param device     The device providing this update.
 *  @param RSSI       The current RSSI of the link.
 *  @param error      If an error occurred, the cause of the failure.
 *
 *  @discussion       This method returns the result of a { readRSSI } call.
 *
 */
- (void)device:(JumaDevice *)device didReadRSSI:(NSNumber *)RSSI error:(NSError *)error;

/*!
 *  @method device:didWriteData:
 *
 *  @param peripheral		The device providing this information.
 *	@param error			If an error occurred, the cause of the failure.
 *
 *  @discussion				This method returns the result of a { writeData:type: } call.
 */
- (void)device:(JumaDevice *)device didWriteData:(NSError *)error;

/*!
 *  @method device:didUpdateData:error:
 *
 *  @param device     The device providing this information.
 *  @param data       The data updated by the device.
 *  @param typeCode   The type code of the updated data.
 *  @param error      If an error occurred, the cause of the failure.
 *
 *  @discussion       This method returns the result of a  { writeData:type: } call.
 *                    If an error occurred, the <i>data</i> will be <i>nil</i>, the <i>typeCode</i> will be <i>-1</i>.
 *
 */
- (void)device:(JumaDevice *)device didUpdateData:(NSData *)data type:(const char)typeCode error:(NSError *)error;

/*!
 *  @method device:didUpdateFirmware:
 *
 *  @param device     The device providing this information.
 *  @param error      If an error occurred, the cause of the failure.
 *
 *  @discussion       This method returns the the result of a { updateFirmware: } call.
 *
 */
- (void)device:(JumaDevice *)device didUpdateFirmware:(NSError *)error;

@end
