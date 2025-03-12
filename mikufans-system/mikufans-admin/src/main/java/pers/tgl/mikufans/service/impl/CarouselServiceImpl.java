package pers.tgl.mikufans.service.impl;

import org.springframework.stereotype.Service;
import pers.tgl.mikufans.mapper.SysCarouselMapper;
import pers.tgl.mikufans.domain.system.SysCarousel;
import pers.tgl.mikufans.service.CarouselService;

@Service
public class CarouselServiceImpl extends BaseServiceImpl<SysCarousel, SysCarouselMapper> implements CarouselService {
//    @Resource
//    @Lazy
//    private VideoService videoService;
//
//    @Override
//    public Carousel saveCarousel(Carousel carousel) {
//        saveOrUpdate(carousel);
//        return carousel;
//    }
//
//    @Override
//    public Page<Carousel> search(CarouselSearch search) {
//        Page<Carousel> page = search.createPage();
//        Date now = new Date();
//        JoinWrappers.lambda(Carousel.class)
//                .selectAssociation(ImageResource.class, Carousel::getMainColor, builder->
//                        builder.result(ImageResource::getMainColor)
//                )
//                .eq(search.getPlayPos() != null, Carousel::getPlayPos, search.getPlayPos())
//                .eq(BooleanUtil.isFalse(search.getIncludeDisabled()), Carousel::getDisabled, 0)
//                .le(BooleanUtil.isFalse(search.getIncludeInactive()), Carousel::getStartDate, now)
//                .ge(BooleanUtil.isFalse(search.getIncludeInactive()), Carousel::getEndDate, now)
//                .leftJoin(ImageResource.class, ImageResource::getId, Carousel::getBannerId)
//                .page(page, Carousel.class);
//        page.getRecords().forEach(carousel -> {
//            if (carousel.getVideoId() != null) {
//                videoService.getVoById(carousel.getVideoId());
//            }
//        });
//        return page;
//    }
}