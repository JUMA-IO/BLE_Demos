//
//  NSTimer+Block.h
//  Effective ObjC
//
//  Created by 汪安军 on 16/5/5.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSTimer (Block)

+ (NSTimer *)juma_timerWithTimeInterval:(NSTimeInterval)ti repeats:(BOOL)yesOrNo block:(void (^)())block;
+ (NSTimer *)juma_scheduledTimerWithTimeInterval:(NSTimeInterval)ti repeats:(BOOL)yesOrNo block:(void (^)())block;

@end
