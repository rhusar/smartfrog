echo PATH Used
echo $PATH
echo Using ENV Properties as
echo ============================
echo INSTALL_DIR
echo $INSTALL_DIR
echo JUNIT_RELEASE_FILE
echo $JUNIT_RELEASE_FILE
echo SF_RELEASE_FILE
echo $SF_RELEASE_FILE
echo SF_RELEASE
echo $SF_RELEASE
echo JUNIT_RELEASE
echo $JUNIT_RELEASE
echo ==============================
if [ $INSTALL_DIR == " " ] ; then 
    echo Unable to get environment variables
    exit 1
fi
cd $INSTALL_DIR
tar -xzf $SF_RELEASE_FILE
mv $SF_RELEASE smartfrog
tar -xzf $JUNIT_RELEASE_FILE
cd $INSTALL_DIR/$JUNIT_RELEASE
mv extras $INSTALL_DIR
mv common.xml $INSTALL_DIR
#cd $INSTALL_DIR/extras/ant
#################################source  $ANT_HOME/bin/ant package
echo Changing to dir $INSTALL_DIR/$JUNIT_RELEASE
cd $INSTALL_DIR/$JUNIT_RELEASE
source  $ANT_HOME/bin/ant
echo Executed Junit Test Cases Exiting...
