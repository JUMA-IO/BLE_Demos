//
//  JumaDevice+OtaModeExtension.h
//  OTA_Tool
//
//  Created by 汪安军 on 16/4/15.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import <JumaBluetoothSDK/JumaBluetoothSDK.h>

@interface JumaDevice (OtaModeExtension)

@property (nonatomic, readonly) BOOL isInOtaMode;

@end
