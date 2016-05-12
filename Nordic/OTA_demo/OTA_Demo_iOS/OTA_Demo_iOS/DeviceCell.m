//
//  DeviceCell.m
//  OTA_Demo_iOS
//
//  Created by 汪安军 on 16/5/12.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import "DeviceCell.h"
#import "JumaDevice+RSSIExtension.h"

@implementation DeviceCell

- (void)awakeFromNib {
    [super awakeFromNib];
    
    self.rssiLabel.text = nil;
    self.nameLabel.text = nil;
    self.nameLabel.adjustsFontSizeToFitWidth = YES;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)setDevice:(JumaDevice *)device {
    self.rssiLabel.text = device.RSSI.stringValue;
    self.nameLabel.text = device.name;
}

@end
