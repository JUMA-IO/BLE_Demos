//
//  JumaManager.h
//  JumaBluetoothSDK
//
//  Created by 汪安军 on 15/7/16.
//  Copyright (c) 2015年 JUMA. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <JumaBluetoothSDK/JumaManagerConstant.h>
@class JumaDevice;

/*!
 *  @enum JumaManagerState
 *
 *  @discussion Represents the current state of a JumaManager instance.
 *
 *  @constant JumaManagerStateUnknown       State unknown, update imminent.
 *  @constant JumaManagerStateResetting     The connection with the system service was momentarily lost, update imminent.
 *  @constant JumaManagerStateUnsupported   The platform doesn't support the Bluetooth Low Energy Central/Client role.
 *  @constant JumaManagerStateUnauthorized  The application is not authorized to use the Bluetooth Low Energy Central/Client role.
 *  @constant JumaManagerStatePoweredOff    Bluetooth is currently powered off.
 *  @constant JumaManagerStatePoweredOn     Bluetooth is currently powered on and available to use.
 *
 */
typedef NS_ENUM(NSInteger, JumaManagerState) {
    JumaManagerStateUnknown = 0,
    JumaManagerStateResetting,
    JumaManagerStateUnsupported,
    JumaManagerStateUnauthorized,
    JumaManagerStatePoweredOff,
    JumaManagerStatePoweredOn,
};


@protocol JumaManagerDelegate;


NS_CLASS_AVAILABLE(NA, 7_0) @interface JumaManager : NSObject

/*!
 *  @property delegate
 *
 *  @discussion The delegate object that will receive central events.
 *
 */
@property (nonatomic, weak) id<JumaManagerDelegate> delegate;

/*!
 *  @property state
 *
 *  @discussion The current state of the manager, initially set to JumaManagerStateUnknown.
 *              Updates are provided by required delegate method { managerDidUpdateState: }.
 *
 */
@property (readonly) JumaManagerState state;

/*!
 *  @property isScanning
 *
 *  @discussion Indicate wether the manager is scanning.
 *
 */
@property (readonly) BOOL isScanning;

/*!
 *  @method initWithDelegate:queue:options:
 *
 *  @param delegate The delegate that will receive manager events.
 *  @param queue    The dispatch queue on which the events will be dispatched.
 *  @param options  An optional dictionary specifying options for the manager.
 *
 *  @discussion     The events of the manager will be dispatched on the provided queue. If <i>nil</i>, the main queue will be used.
 *
 *	@seealso		JumaManagerOptionShowPowerAlertKey
 *	@seealso		JumaManagerOptionIdentifierKey
 *
 */
- (instancetype)initWithDelegate:(id<JumaManagerDelegate>)delegate queue:(dispatch_queue_t)queue options:(NSDictionary *)options  NS_AVAILABLE(NA, 7_0);



/*!
 *  @method retrievePeripheralsWithIdentifiers:
 *
 *  @param UUID	  The UUID string of an device provided by the delegate method { manager:didDiscoverDevice:RSSI: }.
 *
 *  @discussion	  Attempts to retrieve the JumaDevice with the corresponding UUID.
 *
 *	@return		  A JumaDevice objects.
 *
 */
- (JumaDevice *)retrieveDeviceWithUUID:(NSString *)UUID NS_AVAILABLE(NA, 7_0);


/*!
 *  @method scanForDeviceWithOptions:
 *
 *  @param options  An optional dictionary specifying options for the scan.
 *
 *  @discussion     Starts scanning for accessories that are advertising.
 *                  Applications that have specified the 'Uses Bluetooth LE Accessories' background mode are allowed to scan while backgrounded, with one
 *                  caveat:  the JumaManagerScanOptionAllowDuplicatesKey scan option will be ignored.
 *
 *  @see            manager:didDiscoverDevice:RSSI:
 *  @see            JumaManagerScanOptionAllowDuplicatesKey
 */
- (void)scanForDeviceWithOptions:(NSDictionary *)options;

/*!
 *  @method stopScan:
 *
 *  @discussion    Stops scanning for devices.
 *
 *  @see           managerDidStopScan:
 */
- (void)stopScan;




/*!
 *  @method connectDevice:
 *
 *  @param device   The device to be connected.
 *
 *  @discussion     Initiates a connection to <i>device</i>. Connection attempts never time out and, depending on the outcome, will result
 *                  in a call to either { manager:didConnectDevice: } or { manager:didFailToConnectDevice:error: }.
 *                  Pending attempts are cancelled automatically upon deallocation of the device, and explicitly via { disconnectDevice }.
 *
 *  @see            manager:didConnectDevice:
 *  @see            manager:didFailToConnectDevice:error:
 */
- (void)connectDevice:(JumaDevice *)device;
/*!
 *  @method disconnectDevice:
 *
 *  @param device    A JumaDevice.
 *
 *  @discussion      Cancels an active or pending connection to <i>device</i>. Note that this is non-blocking, and any JumaDevice
 *                   commands that are still pending to <i>device</i> may or may not complete.
 *
 *  @see             manager:didDisconnectDevice:code:error:
 */
- (void)disconnectDevice:(JumaDevice *)device;

@end

#pragma mark - JumaManagerDelegate

@protocol JumaManagerDelegate <NSObject>

@required

/*!
 *  @method managerDidUpdateState:
 *
 *  @param manager  The manager whose state has changed.
 *  @param state    The current state of the manager.
 *
 *  @discussion     Invoked whenever the manager's state has been updated. Commands should only be issued when the state is
 *                  JumaManagerStatePoweredOn. A state below <i>JumaManagerStatePoweredOn</i> implies that scanning has stopped
 *                  and any connected devices have been disconnected. If the state moves below <i>JumaManagerStatePoweredOff</i>,
 *                  all JumaDevice objects obtained from this manager become invalid and must be retrieved or discovered again.
 *
 */
- (void)managerDidUpdateState:(JumaManager *)manager;

@optional

/*!
 *  @method manager:didDiscoverDevice:RSSI:
 *
 *  @param manager     The manager providing this update.
 *  @param device      A JumaDevice object.
 *  @param RSSI        The current RSSI of device, in dBm. A value of 127 is reserved and indicates the RSSI was not available.
 *
 *  @discussion        This method is invoked while scanning, upon the discovery of device by manager.
 *
 */
- (void)manager:(JumaManager *)manager didDiscoverDevice:(JumaDevice *)device RSSI:(NSNumber *)RSSI;

/*!
 *  @method managerDidStopScan:
 *
 *  @param manager     The manager providing this update.
 *
 *  @discussion        This method is invoked after { stopScan } called.
 *
 */
- (void)managerDidStopScan:(JumaManager *)manager;


/*!
 *  @method manager:didConnectDevice:
 *
 *  @param manager     The manager providing this information.
 *  @param device      The JumaDevice that has connected.
 *
 *  @discussion        This method is invoked when a connection initiated by { connectDevice: } has succeeded.
 *
 */
- (void)manager:(JumaManager *)manager didConnectDevice:(JumaDevice *)device;
/*!
 *  @method manager:didFailToConnectDevice:error:
 *
 *  @param manager     The manager providing this information.
 *  @param device      The JumaDevice that has failed to connect.
 *  @param error       The cause of the failure.
 *
 *  @discussion        This method is invoked when a connection initiated by { connectDevice: } has failed to complete.
 *                     As connection attempts do not timeout, the failure of a connection is atypical and usually indicative of a transient issue.
 *
 */
- (void)manager:(JumaManager *)manager didFailToConnectDevice:(JumaDevice *)device error:(NSError *)error;
/*!
 *  @method manager:didDisconnectDevice:byRemote:code:error:
 *
 *  @param manager     The manager providing this information.
 *  @param device      The JumaDevice that has disconnected.
 *  @param error       If an error occurred, the cause of the failure.
 *
 *  @discussion        This method is invoked upon the disconnection of a device that was connected by { connectDevice: }. If the disconnection
 *                     was not initiated by { disconnectDevice: }, the cause will be detailed in the <i>error</i> parameter. Once this method has been
 *                     called, no more methods will be invoked on device's JumaDeviceDelegate.
 *
 */
- (void)manager:(JumaManager *)manager didDisconnectDevice:(JumaDevice *)device error:(NSError *)error;

@end
