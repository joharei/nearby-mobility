//
//  ExtensionDelegate.swift
//  Nearby Mobility WatchKit Extension
//
//  Created by Johan Reitan on 09/01/2022.
//

import WatchKit
import shared

class ExtensionDelegate: NSObject, WKExtensionDelegate {
    func applicationDidFinishLaunching() {
        KoinKt.doInitKoin()
    }
}
