//
//  JumaDevice+RSSIExtension.h
//  OTA_Demo_iOS
//
//  Created by 汪安军 on 16/5/12.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import <JumaBluetoothSDK/JumaBluetoothSDK.h>

@interface JumaDevice (RSSIExtension)

@property (nonatomic, strong) NSNumber *RSSI;

@end
