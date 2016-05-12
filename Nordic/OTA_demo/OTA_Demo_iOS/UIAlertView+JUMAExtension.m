//
//  UIAlertView+JUMAExtension.m
//  OTA_Tool
//
//  Created by 汪安军 on 16/4/13.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import "UIAlertView+JUMAExtension.h"

@implementation UIAlertView (JUMAExtension)

- (void)dismissAfterDelay:(NSTimeInterval)delay {
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delay * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self dismiss];
    });
}

- (void)dismiss {
    [self dismissWithClickedButtonIndex:0 animated:YES];
}

- (instancetype)initWithMessage:(NSString *)message cancelButtonTitle:(NSString *)cancelButtonTitle {
    return [self initWithTitle:nil message:message cancelButtonTitle:cancelButtonTitle];
}

- (instancetype)initWithTitle:(NSString *)title cancelButtonTitle:(nullable NSString *)cancelButtonTitle{
    return [self initWithTitle:title message:nil cancelButtonTitle:cancelButtonTitle];
}

- (instancetype)initWithTitle:(NSString *)title message:(NSString *)message cancelButtonTitle:(NSString *)cancelButtonTitle {
    return [self initWithTitle:title message:message delegate:nil cancelButtonTitle:cancelButtonTitle otherButtonTitles:nil];
}

+ (instancetype)alertWithMessage:(NSString *)message cancelButtonTitle:(NSString *)cancelButtonTitle {
    return [self alertWithTitle:nil message:message cancelButtonTitle:cancelButtonTitle];
}

+ (instancetype)alertWithTitle:(NSString *)title cancelButtonTitle:(nullable NSString *)cancelButtonTitle {
    return [self alertWithTitle:title message:nil cancelButtonTitle:cancelButtonTitle];
}

+ (instancetype)alertWithTitle:(NSString *)title message:(NSString *)message cancelButtonTitle:(NSString *)cancelButtonTitle {
    UIAlertView *alertView = [[self alloc] initWithTitle:title message:message cancelButtonTitle:cancelButtonTitle];
    [alertView show];
    return alertView;
}

@end
