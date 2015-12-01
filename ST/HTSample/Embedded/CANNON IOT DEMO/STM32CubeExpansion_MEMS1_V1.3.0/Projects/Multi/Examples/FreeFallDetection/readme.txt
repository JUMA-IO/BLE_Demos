/**
  @page Sensor Demo Application based on Sensor expansion board and STM32 based STM32F401RE Nucleo Boards
  
  @verbatim
  ******************** (C) COPYRIGHT 2014 STMicroelectronics *******************
  * @file    readme.txt  
  * @version V1.3.0
  * @date    04-May-2015
  * @brief   This application contains an example which shows how to obtain data
  *          from various sensors on Sensor expansion board.
  *          The communication is done using a UART connection with PC.
  ******************************************************************************
  *
  * Redistribution and use in source and binary forms, with or without modification,
  * are permitted provided that the following conditions are met:
  *   1. Redistributions of source code must retain the above copyright notice,
  *      this list of conditions and the following disclaimer.
  *   2. Redistributions in binary form must reproduce the above copyright notice,
  *      this list of conditions and the following disclaimer in the documentation
  *      and/or other materials provided with the distribution.
  *   3. Neither the name of STMicroelectronics nor the names of its contributors
  *      may be used to endorse or promote products derived from this software
  *      without specific prior written permission.
  *
  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
  * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
  * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
  * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
  * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  *
  ******************************************************************************
  @endverbatim

@par Example Description 

Main function is to show how to detect the free fall event using the sensor expansion board and a STM32 Nucleo board.
After application is started, the user can try to leave falling the STM32 Nucleo board; when the free fall is detected,
the LED is switched on for a while. The user button can be used to enable/disable the free fall detection feature.


@par Hardware and Software environment

  - This example runs on Sensor expansion board attached to STM32F401RE, STM32L053R8 and STM32L152RE devices
  - You must need the LSM6DS3 DIL24 expansion component to run this example. The example does not work without this component.
  - This example has been tested with STMicroelectronics NUCLEO-F401RE RevC, NUCLEO-L053R8 RevC and NUCLEO-L152RE RevC and 
    can be easily tailored to any other supported device and development board.
    

@par How to use it ? 

This package contains projects for 3 IDEs viz. IAR, µVision and and System Workbench. In order to make the 
program work, you must do the following:
 - WARNING: before opening the project with any toolchain be sure your folder
   installation path is not too in-depth since the toolchain may report errors
   after building.
 - WARNING: before opening the project with System Workbench be sure your folder
   installation path does not include spaces, otherwise the project does not compile correctly.

For IAR:
 - Open IAR toolchain (this firmware has been successfully tested with
   Embedded Workbench V7.30.1).
 - Open the IAR project file EWARM\STM32F401RE-Nucleo\Project.eww or EWARM\STM32L053R8-Nucleo\Project.eww or EWARM\STM32L152RE-Nucleo\Project.eww
   according the target board used.
 - Rebuild all files and load your image into target memory.
 - Run the example.

For µVision:
 - Open µVision 5 toolchain (this firmware has been 
   successfully tested with MDK-ARM Professional Version: 5.14.0.0).
 - Open the µVision project file MDK-ARM\STM32F401RE-Nucleo\Project.uvprojx or MDK-ARM\STM32L053R8-Nucleo\Project.uvprojx or MDK-ARM\STM32L152RE-Nucleo\Project.uvprojx
   according the target board used.
 - Rebuild all files and load your image into target memory.
 - Run the example.

For System Workbench:
 - Open System Workbench for STM32 (this firmware has been 
   successfully tested with System Workbench for STM32 Version 1.1.0.20150310).
 - Set the default workspace proposed by the IDE (please be sure that there are not spaces in the workspace path).
 - Press "File" -> "Import" -> "Existing Projects into Workspace"; press "Browse" in the "Select root directory" and choose the path where the System
   Workbench project is located (it should be SW4STM32\STM32F401RE-Nucleo\STM32F4xx-Nucleo-FreeFallDetection or
   SW4STM32\STM32L053R8-Nucleo\STM32L0xx-Nucleo-FreeFallDetection or SW4STM32\STM32L152RE-Nucleo\STM32L1xx-Nucleo-FreeFallDetection according the target board used). 
 - Rebuild all files and load your image into target memory.
 - Run the example.


 * <h3><center>&copy; COPYRIGHT STMicroelectronics</center></h3>
 */
