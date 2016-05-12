//
//  DeviceCell.h
//  OTA_Demo_iOS
//
//  Created by 汪安军 on 16/5/12.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import <UIKit/UIKit.h>
@class JumaDevice;

@interface DeviceCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel *rssiLabel;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;

- (void)setDevice:(JumaDevice *)device;

@end
