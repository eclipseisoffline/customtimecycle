package xyz.eclipseisoffline.customtimecycle.filefix;

import xyz.eclipseisoffline.filefixutils.api.FileFixInitializer;
import xyz.eclipseisoffline.filefixutils.api.FileFixSchemaRegister;
import xyz.eclipseisoffline.filefixutils.mixin.FileFixerUpperAccessor;

public class CustomTimeCycleFileFixes implements FileFixInitializer {

    @Override
    public void onFileFixPopulate() {
        FileFixSchemaRegister.registerFileFixes(FileFixerUpperAccessor.getFileFixerIntroductionVersion(), TimeManagerFileFix::new);
    }
}
