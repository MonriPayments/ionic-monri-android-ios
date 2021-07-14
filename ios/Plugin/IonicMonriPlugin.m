//
//  IonicMonriPlugin.m
//  App
//
//  Created by Adnan Omerovic on 4. 6. 2021..
//

#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(IonicMonriPlugin, "IonicMonri",
    CAP_PLUGIN_METHOD(confirmPayment, CAPPluginReturnPromise);
)
