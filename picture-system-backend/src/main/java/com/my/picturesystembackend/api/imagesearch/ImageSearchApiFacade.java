package com.my.picturesystembackend.api.imagesearch;

import com.my.picturesystembackend.api.imagesearch.model.ImageSearchResult;
import com.my.picturesystembackend.api.imagesearch.sub.GetImageFirstUrlApi;
import com.my.picturesystembackend.api.imagesearch.sub.GetImageListApi;
import com.my.picturesystembackend.api.imagesearch.sub.GetImagePageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ImageSearchApiFacade {

    /**
     * 搜索图片
     *
     * @param imageUrl imageUrl
     * @return
     */
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl);
        return imageList;
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://haowallpaper.com/link//common/file/previewFileImg/19104168765838208";
        List<ImageSearchResult> imageList = searchImage(imageUrl);
        System.out.println("结果列表" + imageList);
    }
}
