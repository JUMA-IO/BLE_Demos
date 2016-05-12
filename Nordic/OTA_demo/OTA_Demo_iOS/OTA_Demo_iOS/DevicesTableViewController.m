//
//  DevicesTableViewController.m
//  OTA_Demo_iOS
//
//  Created by 汪安军 on 16/5/12.
//  Copyright © 2016年 JUMA. All rights reserved.
//

#import "DevicesTableViewController.h"
#import "DeviceCell.h"
#import "JumaDevice+RSSIExtension.h"

@interface DevicesTableViewController ()

@property (nonatomic, strong) NSMutableArray<JumaDevice *> *devices;
@property (nonatomic, readwrite, strong) JumaDevice *selectedDevice;

@end

@implementation DevicesTableViewController

- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self) {
        _devices = [NSMutableArray array];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Setter and Getter

- (JumaDevice *)selectedDevice {
    
    if (!_selectedDevice && self.devices.count > 0) {
        _selectedDevice = self.devices.firstObject;
    }
    
    return _selectedDevice;
}

#pragma mark - Public

- (void)refreshDevices {
    [self.devices removeAllObjects];
    [self.tableView reloadData];
}

- (void)updateRSSI:(NSNumber *)RSSI forDevice:(JumaDevice *)device {
    
    NSUInteger index = [self.devices indexOfObject:device];
    
    if (index == NSNotFound) {
        device.RSSI = RSSI;
        [self.devices addObject:device];
    } else {
        self.devices[index].RSSI = RSSI;
    }
    
    [self.tableView reloadData];
}

#pragma mark - Table view data source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.devices.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString * identifier = @"DeviceCell";
    JumaDevice *device = self.devices[indexPath.row];
    
    DeviceCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier forIndexPath:indexPath];
    [cell setDevice:device];
    
    if (device == self.selectedDevice) {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
    } else {
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
    
    return cell;
}

#pragma mark - <UITableViewDelegate>

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    self.selectedDevice = self.devices[indexPath.row];
    
    [tableView reloadData];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
