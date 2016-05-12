//
//  UIAlertView+JUMAExtension.h
//  OTA_Tool
//
//  Created by 汪安军 on 16/4/13.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIAlertView (JUMAExtension)

- (instancetype)initWithMessage:(nullable NSString *)message cancelButtonTitle:(nullable NSString *)cancelButtonTitle;
- (instancetype)initWithTitle:(nullable NSString *)title cancelButtonTitle:(nullable NSString *)cancelButtonTitle;
- (instancetype)initWithTitle:(nullable NSString *)title message:(nullable NSString *)message cancelButtonTitle:(nullable NSString *)cancelButtonTitle;

+ (instancetype)alertWithMessage:(nullable NSString *)message cancelButtonTitle:(nullable NSString *)cancelButtonTitle;
+ (instancetype)alertWithTitle:(nullable NSString *)title cancelButtonTitle:(nullable NSString *)cancelButtonTitle;
+ (instancetype)alertWithTitle:(nullable NSString *)title message:(nullable NSString *)message cancelButtonTitle:(nullable NSString *)cancelButtonTitle;

- (void)dismiss;
- (void)dismissAfterDelay:(NSTimeInterval)delay;

@end

NS_ASSUME_NONNULL_END
