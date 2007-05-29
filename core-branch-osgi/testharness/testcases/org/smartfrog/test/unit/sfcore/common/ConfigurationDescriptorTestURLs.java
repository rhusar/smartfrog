/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */

package org.smartfrog.test.unit.sfcore.common;

public interface ConfigurationDescriptorTestURLs {

//    String url1 = "display:TER:::localhost:subprocess";
//    String url1b = "\"HOST guijarro-j-3.hpl.hp.com:rootProcess:sfDefault:display\":TER:::localhost:subprocess";
//    String url2 = "display:TER:::localhost:subprocess";
//    String url2b = "\"HOST guijarro-j-3.hpl.hp.com:rootProcess:sfDefault:display\":DETaTER:::localhost:subprocess";
//    String url3 = "display:TERMINATE:::localhost:subprocess";
//    String url3b = "\"HOST guijarro-j-3.hpl.hp.com:rootProcess:sfDefault:display\":TERMINATE:::localhost:subprocess";
//    String url4 = "display:DEPLOY:URL:SFCONFIG:localhost:subprocess";
//    String url4b = "\"HOST guijarro-j-3.hpl.hp.com:rootProcess:sfDefault:display\":DEPLOY:URL:SFCONFIG:localhost:subprocess";
//    String url5 = "display:DETaTER:URL:SFCONFIG:localhost:";
//    String url5b = "\"HOST guijarro-j-3.hpl.hp.com:rootProcess:sfDefault:display\":DETaTERM:URL:SFCONFIG:localhost:";
//    String url6 = "display:DEPLOY:URL:SFCONFIG:localhost:";
//    String url6b = "\"HOST guijarro-j-3.hpl.hp.com:rootProcess:sfDefault:display\":DETACH:URL:SFCONFIG:localhost:";
//    String url7 = "app:DEPLOY:d:\\cvs\\SmartFrog\\core\\extras\\ant\\test\\files\\valid.sf::localhost:";

    //Successful
    String urlTest01  =":DEPLOY:org/smartfrog/examples/counter/example.sf::localhost:";
    String urlTest02  ="Julio:DEPLOY:org/smartfrog/examples/counter/example.sf::localhost:";
    String urlTest03  ="\"HOST localhost:sfDefault:Julio1\":DEPLOY:org/smartfrog/examples/counter/example.sf::localhost:";
    String urlTest04  ="\"HOST \"127.0.0.1\":sfDefault:Julio1B\":DEPLOY:org/smartfrog/examples/counter/example.sf::localhost:";
    String urlTest05  ="\"HOST localhost:Julio2\":DEPLOY:org/smartfrog/examples/counter/example.sf::localhost:";
    String urlTest06  ="\"HOST localhost:rootProcess:Julio3\":DEPLOY:org/smartfrog/examples/counter/example.sf::localhost:";
    //With displays on
//    String urlTest07   ="\"HOST localhost:sfDefault:SubProcessInDefault1\":DEPLOY:org/smartfrog/examples/subprocesses/subprocess.sf::localhost:";
    //With displays off
    String urlTest07   ="\"HOST localhost:sfDefault:SubProcessInDefault1\":DEPLOY:org/smartfrog/test/system/deploy/subprocessTestHarness.sf::localhost:";
    String urlTest07b  ="\"HOST localhost:sfDefault:SubProcessInDefault1\":TERMINATE:::localhost:";

    String urlTest08  ="\"HOST localhost:first:CounterInFirst1ROOT\":DEPLOY:org/smartfrog/examples/counter/example.sf::localhost:";
    String urlTest09  ="\"HOST localhost:first:CounterInFirst1FIST\":DEPLOY:org/smartfrog/examples/counter/example.sf::localhost:first";
    String urlTest10  ="counterEx3:DEPLOY:org/smartfrog/examples/counter/example2.sf:\"testLevel1:counterToSucceed\":localhost:";
    String urlTest11  ="\"HOST \"127.0.0.1\":first:Julio2B\":DEPLOY:org/smartfrog/examples/counter/example2.sf:\"testLevel1:counterToSucceed\":localhost:";
    String urlTest12  ="\"HOST localhost:SubProcessInRootProcess2\":DEPLOY:org/smartfrog/examples/subprocesses/subprocess.sf::localhost:";
    String urlTest12b  ="\"HOST localhost:SubProcessInRootProcess2\":TERMINATE:org/smartfrog/examples/subprocesses/subprocess.sf::localhost:";
    String urlTest14  ="\"ProcessName\":DEPLOY:\"C:\\Documents and Settings\\julgui\\My Documents\\testkk5.sf\"::127.0.0.1:";
    String urlTest100  =":TERMINATE:::localhost:";

    //Unsuccessful
    String urlTestN01 ="\"HOST 127.0.0.1:sfDefault:Julio1A\":DEPLOY:org/smartfrog/examples/counter/example.sf::localhost:";
    String urlTestN02 ="::::";
    String urlTestN03 =":";
    String urlTestN04 ="::";
    String urlTestN05 =":::";
    String urlTestN06 =":::::";
    String urlTestN07  ="\"HOST localhost:sfDefault:\":DEPLOY:org/smartfrog/examples/counter/example.sf::localhost:";
    String urlTestN08 ="\"HOST localhost:rootProcess:\":DEPLOY:org/smartfrog/examples/counter/example.sf::localhost:";

    String fileURLTest01 = "org/smartfrog/test/system/deploy/deploySystemTest.sfcd";
    // String url  ="";

}
