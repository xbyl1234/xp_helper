package com.hook.okhttp_redirect;

import com.common.units;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ResourceCacheTest implements ResourceCacheInterface {

    //查询是否有缓存
    @Override
    public boolean HasCache(CacheId id) throws Throwable {
        return true;
    }

    //下载缓存
    @Override
    public Cache GetCache(CacheId id) throws Throwable {
        String data ="{\n" +
                "\"headers\":[\"content-type\", \"application/protobuf\", \"vary\", \"Sec-Fetch-Dest, Sec-Fetch-Mode, Sec-Fetch-Site\", \"report-to\", \"{\\\"group\\\":\\\"nel\\\",\\\"max_age\\\":300,\\\"endpoints\\\":[{\\\"url\\\":\\\"https:\\\\/\\\\/beacons.gcp.gvt2.com\\\\/domainreliability\\\\/upload-nel\\\",\\\"priority\\\":1,\\\"weight\\\":1},{\\\"url\\\":\\\"https:\\\\/\\\\/beacons.gvt2.com\\\\/domainreliability\\\\/upload-nel\\\",\\\"priority\\\":1,\\\"weight\\\":1},{\\\"url\\\":\\\"https:\\\\/\\\\/beacons5.gvt3.com\\\\/domainreliability\\\\/upload-nel\\\",\\\"priority\\\":2,\\\"weight\\\":1}]}\", \"nel\", \"{\\\"report_to\\\":\\\"nel\\\",\\\"max_age\\\":300,\\\"include_subdomains\\\":true,\\\"success_fraction\\\":0.05,\\\"failure_fraction\\\":1.0}\", \"content-encoding\", \"gzip\", \"cache-control\", \"no-cache, no-store, max-age=0, must-revalidate\", \"pragma\", \"no-cache\", \"expires\", \"Mon, 01 Jan 1990 00:00:00 GMT\", \"date\", \"Sat, 21 Oct 2023 09:25:25 GMT\", \"x-dfe-content-length\", \"1040\", \"cross-origin-resource-policy\", \"same-site\", \"cross-origin-opener-policy\", \"same-origin\", \"content-security-policy\", \"require-trusted-types-for 'script';report-uri /_/PlayGatewayHttp/cspreport\", \"accept-ch\", \"Sec-CH-UA-Arch, Sec-CH-UA-Bitness, Sec-CH-UA-Full-Version, Sec-CH-UA-Full-Version-List, Sec-CH-UA-Model, Sec-CH-UA-WoW64, Sec-CH-UA-Form-Factor, Sec-CH-UA-Platform, Sec-CH-UA-Platform-Version\", \"permissions-policy\", \"ch-ua-arch=*, ch-ua-bitness=*, ch-ua-full-version=*, ch-ua-full-version-list=*, ch-ua-model=*, ch-ua-wow64=*, ch-ua-form-factor=*, ch-ua-platform=*, ch-ua-platform-version=*\", \"server\", \"ESF\", \"x-xss-protection\", \"0\", \"x-frame-options\", \"SAMEORIGIN\", \"x-content-type-options\", \"nosniff\", \"alt-svc\", \"h3=\\\":443\\\"; ma=2592000,h3-29=\\\":443\\\"; ma=2592000\"],\n" +
                "\"data\":\"CoQN8gWADQphCgRhZHdjElkKABIbChkaFwoTChEKDVJFU1BPTlNFX0NPREUoABABGgAgvAU6M6Kt17cELXIrCggiBgoEYWRudxIdoq3XtwQXchUKCCIGCgRhZHN3EgciBQoDdW12QgBKAAr/AQoEYWRudxL2AQoDCLAxEhsKGRoXChMKEQoNUkVTUE9OU0VfQ09ERSgAEAEaDzUAAFJD6sTclwUECgIIACC8BcrQr58FuQEKd8ICdApSEkQVAAAgQh0AACBCRQAAgEFNAACAQHIG65Kk66GcogEZGhcKEwoRCg1SRVNQT05TRV9DT0RFKAAQAfABDtACA/gCCoADCioKCBUaBhgwIDBIFRIeGg+wAQToAgbwAgX4AgaAAwUiCzACiAEEuAISyAIKEim6AiYSByIFCgN1bXYaGRoXChMKEQoNUkVTUE9OU0VfQ09ERSgAEAFSABoTEgzoAgbwAgP4AgaAAwO6AQIQAQr/AQoEYWRzdxL2AQoDCL4xEhsKGRoXChMKEQoNUkVTUE9OU0VfQ09ERSgAEAEaDzUAAFJD6sTclwUECgIIACC8BcrQr58FuQEKd8ICdApSEkQVAAAgQh0AACBCRQAAgEFNAACAQHIG65Kk66GcogEZGhcKEwoRCg1SRVNQT05TRV9DT0RFKAAQAfABDtACA/gCCoADCioKCBUaBhgwIDBIFRIeGg+wAQToAgbwAgX4AgaAAwUiCzACiAEEuAISyAIKEim6AiYSByIFCgN1bXYaGRoXChMKEQoNUkVTUE9OU0VfQ09ERSgAEAFKABoTEgzoAgbwAgP4AgaAAwO6AQIQAQpfCgN1bXYSWAoAEhsKGRoXChMKEQoNUkVTUE9OU0VfQ09ERSgAEAEaACC8BToyoq3XtwQscioKBiIECgJ1bRIeggEbEhkaFwoTChEKDVJFU1BPTlNFX0NPREUoABABOgAKnAEKAnVtEpUBCgASGwoZGhcKEwoRCg1SRVNQT05TRV9DT0RFKAAQARoPNQAAKEPqxNyXBQQKAggAILwFytCvnwVcCgPCAgASQLoCPRIeggEbEhkaFwoTChEKDVJFU1BPTlNFX0NPREUoABABGhkaFwoTChEKDVJFU1BPTlNFX0NPREUoABABOgAaExIM6AIG8AID+AIGgAMDugECEAEamwQ4AEJrCmkIAhADGg8QhbLOqQYZjV1wQeZM2UEiTwobChVjb20ud2VtYWRlLm5pZ2h0Y3Jvd3MQARgDEAEY7ci4wImD0+tJKh0SG1pwck1WbGpwSElxYkVET1BKdHZoR1drWjRab2Dx+raMtTEyATNK5gJzaWduYXR1cmU9QUJHNF9XWEo3ckx5amxBanZkdWNyQ29fejlxUzdqX1pHaGZQOUQ5VXc0d2R1bk5SZy1ySGJLR3h3MVJGTjFYVi1RaVdaeTNRVDlNSndWRkV6YmR4QnR2N2QybzRhNnE0bnRCMzJpbVZBSlcwX2ltTEtrZ2ZvSHZMMXZqOElPSmpmYjFLdmlqWDRMcTdFaG04NERFSERDaDdOVzVLekxzNll0ZEhQdjZDYTVJRm1oelo0ZGpSSHJJTFY0c251eFgwV0tWV1pUN1lOWjV2ZldIcms4WGxId2hkOVFQQkpKWU1CSktEWlk0RzZILS1Vc0t3X0tPNjJjWUxUNWI3eFZRTGxjaExrTkRiT05pODdhdFFVOF90UFRBQVR6Mmc5eW1WX3Q1WkZ5V2wyYWVoN1JISnNiZGswNXc4MnBQUGJnbGtyOTlFVGlHd0Q4b1hGTXRBTUYyV09FUEhtVnFtUhcIARITChEKDVJFU1BPTlNFX0NPREUoAGIoChZmcmVlX2FjcXVpc2l0aW9uX2dhbWVzEg4KDAoIcXVhbnRpdHkgASJ4CkoKGQoVY29tLndlbWFkZS5uaWdodGNyb3dzEAcSBgoEQ0FFPSolChkKFWNvbS53ZW1hZGUubmlnaHRjcm93cxAHEgYKBENBRT0oARIAKgIIAToCCABaAhABsgEWChQtMzU3ODI3MzQzNDI2NTIwMzg0MZoCAgoAMmYIABJgCAUyXAgCEgIIABodEhsKFWNvbS53ZW1hZGUubmlnaHRjcm93cxABGAMgASoHCAASA1hYWFoAkgEAmAEAsAEAugEAigIULTM1NzgyNzM0MzQyNjUyMDM4NDHSAgQIARAAGAFCFSITCgRhZHdjQgtSCRIHCIwFEgAoAVABKgMIyAZKHggSmgEZChMI35eNjemGggMVTFqWCh3TOgIJEAEgAA==\"\n" +
                "}";
        return new Cache(id, data.getBytes());
    }

    //上次缓存
    @Override
    public boolean UploadCache(Cache cache, String path) throws Throwable {
        cache.id.path = path;
        units.save_file(path, cache.ToJson().toJSONString().getBytes(StandardCharsets.UTF_8));
        return true;
    }
}
