/*
  Copyright 2014-2015 juma.io

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

#include "juma_sdk_api.h"
#include "LIS2DH12.h"
#include "stdio.h"

#define WHO_AM_I 0x33
uint8_t lis2dh12;
uint8_t ble;


void gpio_test(void * args)
{
  static uint8_t statue;
  uint8_t i;
  
  for(i=0; i<5; i++)
  {
    gpio_setup(i, GPIO_OUTPUT);
    gpio_write(i, 0);
  }
  
  statue ++;
  
  gpio_write(statue%5, 1);
  
  run_after_delay(gpio_test, NULL, 100);
}

//void
void led_blink_one(void * args)
{
  static uint8_t statue;
  statue ++;
  if(statue >= 6)
  {
    statue = 0;
  }
  
  gpio_setup(5, GPIO_OUTPUT);
  gpio_setup(6, GPIO_OUTPUT);
  gpio_setup(7, GPIO_OUTPUT);
  
  gpio_write(5, 1);
  gpio_write(6, 1);
  gpio_write(7, 1);
  
  if(statue == 0)
  {
    gpio_write(5, 0);
  }
  
  if(statue == 2)
  {
    gpio_write(6, 0);
  }
  
  if(statue == 4)
  {
    gpio_write(7, 0);
  }
  
  run_after_delay(led_blink_one, NULL, 100);
}

void statue_ok(void * args)
{
  if(lis2dh12 && ble)
  {
    run_after_delay(led_blink_one, NULL, 100);
  }
  else
  {
    run_after_delay(statue_ok, NULL, 100);
  }
}

int16_t acc_value;

void lis2dh12_test03(void * args)
{
  int16_t acc_value_new;
  
  acc_value_new = LIS2DH12_Get_Chan_Data(LIS2DH12_ACC_CHAN_X);
  
//  ble_device_send(0x01, 2, (uint8_t *)&acc_value);
//  ble_device_send(0x01, 2, (uint8_t *)&acc_value_new);
  
  if((acc_value_new - acc_value) > 0x50)
  {
    lis2dh12 = 1;
  }
}

void lis2dh12_test02(void * args)
{
  acc_value = LIS2DH12_Get_Chan_Data(LIS2DH12_ACC_CHAN_X);
    
  LIS2DH12_SET_SELF_TEST_MODE(LIS2DH12_SELF_TEST_MODE_MODE1);
  
  run_after_delay(lis2dh12_test03, NULL, 100);
}

void lis2dh12_test01(void * args)
{
  if(WHO_AM_I == LIS2DH12_Read_Reg(0x0f))
  {

    LIS2DH12_SET_SELF_TEST_MODE(LIS2DH12_SELF_TEST_MODE_DISABLE);
    
    run_after_delay(lis2dh12_test02, NULL, 100);
  }
}

void on_ready()
{
  lis2dh12 = 0;
  ble = 0;

  LIS2DH12_InitStruct LIS = 
    {
       .MISO = 10,
       .MOSI = 9,
       .CSN = 11,
       .SCK = 12,
       .INT1 = 13,
       .INT2 = 14,
       .FREQUENCY = LIS2DH12_FREQUENCY, 
    }; 
    
  LIS2DH12_Config(& LIS);  
    
  LIS2DH12_Set_Data_Rate(LIS2DH12_DATA_RAT_10HZ);  

  ble_device_set_advertising_interval(2000);
  ble_device_set_name("JUMA_Acc");
  ble_device_start_advertising();
  
  run_after_delay(statue_ok, NULL, 100);
  run_after_delay(gpio_test, NULL, 200);
}

void ble_device_on_connect(void)
{

}

void ble_device_on_disconnect(uint8_t reason)
{
  ble_device_start_advertising();
}

void ble_device_on_message(uint8_t type, uint16_t length, uint8_t* value)
{
  if(value[0] == 0x5A)
  {
    ble = 1;
    run_after_delay(lis2dh12_test01, NULL, 100);
  }
}

