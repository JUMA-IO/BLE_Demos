//
//  JumaManagerConstant.h
//  JumaBluetoothSDK
//
//  Created by 汪安军 on 15/7/17.
//  Copyright (c) 2015年 JUMA. All rights reserved.
//

#import <Foundation/Foundation.h>

#ifndef JumaSDK_JumaManagerConstant_h
#define JumaSDK_JumaManagerConstant_h

/*!
 *  @const  JumaManagerOptionShowPowerAlertKey
 *
 *  @discussion A NSNumber (Boolean) indicating that the system should, if Bluetooth is powered off when JumaManager is instantiated, display
 *				a warning dialog to the user.
 *
 *  @see		initWithDelegate:queue:options:
 *
 */
FOUNDATION_EXPORT NSString * const JumaManagerOptionShowPowerAlertKey NS_AVAILABLE(NA, 7_0);

/*!
 *  @const  JumaManagerOptionRestoreIdentifierKey
 *
 *  @discussion A NSString containing a unique identifier (UID) for the JumaManager that is being instantiated. This UID is used
 *				by the system to identify a specific JumaManager instance for restoration and, therefore, must remain the same for
 *				subsequent application executions in order for the manager to be restored.
 *
 *  @see		initWithDelegate:queue:options:
 *
 */
FOUNDATION_EXPORT NSString * const JumaManagerOptionRestoreIdentifierKey NS_AVAILABLE(NA, 7_0);


/*!
 *  @const JumaManagerScanOptionAllowDuplicatesKey
 *
 *  @discussion A NSNumber (Boolean) indicating that the scan should run without duplicate filtering. By default, multiple discoveries of the
 *              same device are coalesced into a single discovery event. Specifying this option will cause a discovery event to be generated
 *				every time the device is seen, which may be many times per second. This can be useful in specific situations, such as making
 *				a connection based on a device's RSSI, but may have an adverse affect on battery-life and application performance.
 *
 *  @see        scanFordeviceWithOptions:
 *
 */
FOUNDATION_EXPORT NSString * const JumaManagerScanOptionAllowDuplicatesKey;


#endif
