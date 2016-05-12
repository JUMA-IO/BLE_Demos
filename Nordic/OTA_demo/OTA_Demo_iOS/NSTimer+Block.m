//
//  NSTimer+Block.m
//  Effective ObjC
//
//  Created by 汪安军 on 16/5/5.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import "NSTimer+Block.h"

@implementation NSTimer (Block)

+ (NSTimer *)juma_timerWithTimeInterval:(NSTimeInterval)ti repeats:(BOOL)yesOrNo block:(void (^)())block {
    return [self timerWithTimeInterval:ti target:self selector:@selector(blockInvoke:) userInfo:[block copy] repeats:yesOrNo];
}

+ (NSTimer *)juma_scheduledTimerWithTimeInterval:(NSTimeInterval)ti repeats:(BOOL)yesOrNo block:(void (^)())block {
    return [self scheduledTimerWithTimeInterval:ti target:self selector:@selector(blockInvoke:) userInfo:[block copy] repeats:yesOrNo];
}

+ (void)blockInvoke:(NSTimer *)timer {
    
    void (^blcok)() = timer.userInfo;
    if (blcok) {
        blcok();
    }
}

@end
