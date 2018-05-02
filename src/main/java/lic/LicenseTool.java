package lic;

import com.javax0.license3j.licensor.License;
import org.bouncycastle.openpgp.PGPException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class LicenseTool {
    public static void checkLicense(String licenseFile,String pubringFile,byte[] digest)  {
        License lic = parseLicense(licenseFile,pubringFile,digest);
        checkDateAndVersionValidity(lic);
    }
    protected static void checkDateAndVersionValidity(License lic) {
        String expiredDate = lic.getFeature(LicenseBase.expireDateKey);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar licExpiredCal = new GregorianCalendar();
        try {
            licExpiredCal.setTime(sdf.parse(expiredDate));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ExpiredDate parse Error");
        }
        Calendar nowCal = new GregorianCalendar();
        nowCal.setTime(new Date());
        if (!(licExpiredCal.getTimeInMillis()>nowCal.getTimeInMillis())) {
            throw new IllegalArgumentException(
                    "Your License  expired  on "+expiredDate+", Please contact " + lic.getFeature(LicenseBase.licensedByKey));
        }
    }
    protected static  License parseLicense(String licenseFile,String pubringFile,byte[] digest){
        License lic = new License();
        try {
            lic.loadKeyRingFromResource(pubringFile, digest);
            lic.setLicenseEncodedFromFile(licenseFile);
        }  catch (PGPException e) {
            e.printStackTrace();
            throw new RuntimeException("License File is not valid!");
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("License File or PublicRing File not Found!");
        }
        return lic;
    }

    public static void main(String[] args) {
        URL url = LicenseTool.class.getClassLoader().getResource("");
        File f = new File(url.getFile());
        String samplesFolder = f.getAbsolutePath();
//license文件目录
        String licenseFile = samplesFolder+"\\my.lic";
        //公钥目录,必须放在class目录下，不能用绝对路径
        String pubringFile = "hykt-public.asc";
        byte[] digest = null;
        LicenseTool.checkLicense(licenseFile,pubringFile,digest);
    }
}

