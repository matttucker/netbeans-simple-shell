/**
* The contents of this file are subject to the terms of the Common Development
* and Distribution License (the License). You may not use this file except in
* compliance with the License.
*
* You can obtain a copy of the License at http://www.netbeans.org/cddl.html
* or http://www.netbeans.org/cddl.txt.
*
* When distributing Covered Code, include this CDDL Header Notice in each file
* and include the License file at http://www.netbeans.org/cddl.txt.
* If applicable, add the following below the CDDL Header, with the fields
* enclosed by brackets [] replaced by your own identifying information:
* "Portions Copyrighted [year] [name of copyright owner]"
*
* The Original  Software is Simple Shell. The Initial Developer of the Original
* Software is Sun Microsystems, Inc.
*
* Matt Tucker, Sun Microsystems
*
*/

importPackage(Packages.com.sun.datastorage.shell);


print('Evaluating shell.js ...');

/**
 * shell
 * 
 * Send command string to the shell for evaluation.
 * 
 */
function shell(argStr) {
    shellDoc.processCmd(argStr);
}


/**
 * rm
 * 
 * Removes a file.
 *
 * Inputs:
 *
 * Returns: 
 *
 * Options:
 *
 * Examples:
 *   rm('foo.js')
 */
function rm(argStr) {
    if(argStr != undefined) {
        shellCommander.parse('rm ' + argStr);
    } else {
        shellCommander.parse('rm');
    }
}


/**
 * evalf
 *  
 * Evaluate a file with the script engine.
 *
 * Example:
 *    evalf('shell.js');
 */
function evalf(argStr) {
    if(argStr != undefined) {
        shellCommander.parse('evalf ' + argStr);
    } else {
        shellCommander.parse('evalf');
    }
}

/**
 * getCwd
 *
 * Get the *shell* current working directory.
 *
 */
function getCwd() {
    return shellCommander.getCwd();
}



/**
 * 
 *  Utility functions.
 *
 */


/**
 *
 */
function checkCancelled() {
    shellCommander.checkCancelled();
    java.lang.Thread.sleep(100);
}
/**
 * printArray()
 *
 * Print an array to the shell.
 */
function printArray(value) {
    print('[');
    for (var i= 0; i < value.length; i++) {
        print(value[i]);
        
        if (i != value.length - 1)
            print(', ');    
    }
    println(']');
}

var ticTime = 0.0;

/**
 * tic
 * 
 * Start a stopwatch timer. 
 * 
 * "tic" and "toc" functions work together to measure 
 * elapsed time. "tic" saves the current time that "toc" uses later to measure
 * the elapsed time. The sequence of commands:
 *    
 *              tic()
 *              operations
 *              elapsedTime = toc()
 * 
 * measures the amount of time shell takes to complete the one
 * or more operations specified here by "operations". "elapsedTime" is in 
 * seconds.
 *
 * Idea and comments inspired from Matlab's tic and toc commands.
 */
function tic() {
    ticTime = java.lang.System.currentTimeMillis();
}

/**
 * tic
 * 
 * Read the stopwatch timer. 
 *
 * "tic" and "toc" functions work together to measure 
 * elapsed time. "tic" saves the current time that "toc" uses later to measure
 * the elapsed time. The sequence of commands:
 *    
 *              tic()
 *              operations
 *              elapsedTime = toc()
 * 
 * measures the amount of time shell takes to complete the one
 * or more operations specified here by "operations". "elapsedTime" is in 
 * seconds.
 *
 * Idea and comments inspired from Matlab's tic and toc commands.
 *
 * Returns time in seconds since "toc()"
 */
function toc() {
    tocTime = java.lang.System.currentTimeMillis();
    return (tocTime - ticTime)/1000;
}

/**
 * tocms
 *
 * Same as toc() but returns time in milliseconds since "tic()"
 */
function tocms() {
    tocTime = java.lang.System.currentTimeMillis();
    return (tocTime - ticTime);
}

/**
 * Pause for a specified number of seconds.
 *
 * Inputs:
 * numSec  Number of seconds to pause for.
 *
 */
function pause(numSec) {
    
    var mSec = Math.floor(numSec*1000);
    var n = Math.floor(mSec /200);
    var rem = mSec % 200;

    for (var i = 0 ; i < n ; i++) {
        java.lang.Thread.sleep(200);
        //TODO: show on GUI somehow- like a blinking object.
        //print(".");
        checkCancelled();
    }
    // do the remainder
    java.lang.Thread.sleep(rem);
}
/**
 * Sleep for a specified number of milli-seconds.
 *
 * Inputs:
 * mSec  Number of milliseconds to sleep for.
 *
 */
function sleep(mSec) {
    java.lang.Thread.sleep(mSec);
    checkCancelled();
}

/**
 * version information.
 *
 * Inputs:
 * property key
 *
 */
function version(key) {
    if(key != undefined) {
        ANSWER = VersionManager.getVersionProperty(key);
    } else {
        var name = VersionManager.getVersionProperty("app_name");
        var version = VersionManager.getVersionProperty("app_version");
        var build = VersionManager.getVersionProperty("app_build");
        var str =
        ANSWER = name + ' ' + version + ' (Build '+ build + ')';
        println(ANSWER);
    }   
    return ANSWER;
}




println('done.');
