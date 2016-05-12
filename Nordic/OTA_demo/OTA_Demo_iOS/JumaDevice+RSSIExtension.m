//
//  JumaDevice+RSSIExtension.m
//  OTA_Demo_iOS
//
//  Created by 汪安军 on 16/5/12.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import "JumaDevice+RSSIExtension.h"
@import ObjectiveC;

@implementation JumaDevice (RSSIExtension)

- (NSNumber *)RSSI {
    return objc_getAssociatedObject(self, @selector(RSSI));
}

- (void)setRSSI:(NSNumber *)RSSI {
    objc_setAssociatedObject(self, @selector(RSSI), RSSI, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

@end
