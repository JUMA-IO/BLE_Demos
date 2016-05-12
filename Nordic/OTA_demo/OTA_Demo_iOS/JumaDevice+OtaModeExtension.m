//
//  JumaDevice+OtaModeExtension.m
//  OTA_Tool
//
//  Created by 汪安军 on 16/4/15.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import "JumaDevice+OtaModeExtension.h"

static NSString * const kDeviceOtaName = @"OTA Mode";

@implementation JumaDevice (WAJ)

- (BOOL)isInOtaMode {
    return [self.name isEqualToString:kDeviceOtaName];
}

@end
