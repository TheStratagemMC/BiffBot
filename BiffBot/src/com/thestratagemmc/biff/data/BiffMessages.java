package com.thestratagemmc.biff.data;

import com.google.common.io.Files;
import com.thestratagemmc.biff.BiffPlugin;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Axel on 1/29/2016.
 */
public class BiffMessages extends BiffDataHandler{

    @Override
    public String getDefaultDirectory() {
        return "messages";
    }

}
