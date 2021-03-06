package com.hubert.xu.zmvp.mvp.model;

import com.hubert.xu.zmvp.constant.Constants;
import com.hubert.xu.zmvp.http.BaseObserver;
import com.hubert.xu.zmvp.http.RetrofitClient;
import com.hubert.xu.zmvp.http.api.ApiService;
import com.hubert.xu.zmvp.mvp.model.entity.AllRankTypeBean;
import com.hubert.xu.zmvp.mvp.model.entity.BookClassifyBean;
import com.hubert.xu.zmvp.mvp.model.entity.BookClassifyLv2Bean;
import com.hubert.xu.zmvp.mvp.model.entity.BookListTagBean;
import com.hubert.xu.zmvp.mvp.model.entity.BookTagBean;
import com.hubert.xu.zmvp.mvp.model.entity.BookclassifyLocalBean;
import com.hubert.xu.zmvp.mvp.model.entity.CommentDetailBean;
import com.hubert.xu.zmvp.mvp.model.entity.CommentListBean;
import com.hubert.xu.zmvp.mvp.model.entity.DiscussListBean;
import com.hubert.xu.zmvp.mvp.model.entity.HotReviewBean;
import com.hubert.xu.zmvp.mvp.model.entity.LocalAllRankingTypeBean;
import com.hubert.xu.zmvp.mvp.model.entity.LocalBookTagsBean;
import com.hubert.xu.zmvp.mvp.model.entity.LocalBookdetailBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: Hubert.Xu
 * Date  : 2017/10/12
 * Desc  :
 */

public class RemoteDataManager {

    private static final RemoteDataManager INSTANCE = new RemoteDataManager();
    private final ApiService mApiService;

    public RemoteDataManager() {
        mApiService = RetrofitClient.getInstance().create(ApiService.class);
    }

    public static RemoteDataManager getInstance() {
        return INSTANCE;
    }

    public void getRankingList(String rankingId, BaseObserver observer) {
        mApiService.getRanking(rankingId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getGirlBookList(HashMap defaultParamsMap, BaseObserver observer) {
        mApiService.getGirlBookList(defaultParamsMap).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getDiscussList(HashMap defaultParamsMap, BaseObserver observer) {
        mApiService.getDiscussList(defaultParamsMap).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getBookTag(BaseObserver observer) {
        mApiService.getBookListTags().map(booktags -> {
            LocalBookTagsBean localBookTagsBean = new LocalBookTagsBean();
            localBookTagsBean.ok = true;
            List<LocalBookTagsBean.BookTag> localBookTags = new ArrayList<>();
            localBookTags.add(new LocalBookTagsBean.BookTag("性别", Constants.BOOK_TYPE_SIGN));
            localBookTags.add(new LocalBookTagsBean.BookTag("男生", Constants.BOOK_TYPE_NAME));
            localBookTags.add(new LocalBookTagsBean.BookTag("女生", Constants.BOOK_TYPE_NAME));
            for (BookTagBean.DataBean data : booktags.getData()) {
                localBookTags.add(new LocalBookTagsBean.BookTag(data.getName(), Constants.BOOK_TYPE_SIGN));
                for (String tag : data.getTags()) {
                    localBookTags.add(new LocalBookTagsBean.BookTag(tag, Constants.BOOK_TYPE_NAME));
                }
            }
            localBookTagsBean.setBookTags(localBookTags);
            return localBookTagsBean;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getBookReviewList(HashMap defaultParamsMap, BaseObserver observer) {
        mApiService.getBookReviewList(defaultParamsMap).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getBookList(HashMap map, BaseObserver observer) {
        mApiService.getBookList(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getBookListDetail(String bookListId, BaseObserver observer) {
        mApiService.getBookListDetail(bookListId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getBookHelpList(HashMap defaultParamsMap, BaseObserver observer) {
        mApiService.getBookHelpList(defaultParamsMap).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getBookClassify(BaseObserver<BookclassifyLocalBean> observer) {
        Observable.zip(mApiService.getBookClassify(), mApiService.getBookClassifyLv2(), (bookClassifyBean, bookClassifyLv2Bean) -> {
            BookclassifyLocalBean bookClassifyLocalData = new BookclassifyLocalBean();
            List<BookclassifyLocalBean.LocalBookClassifyBean> localBookClassifyList = new ArrayList<>();
            localBookClassifyList.add(new BookclassifyLocalBean.LocalBookClassifyBean(Constants.BOOK_TYPE_SIGN, "", "男生", 0, null));
            for (BookClassifyBean.MaleBean male : bookClassifyBean.getMale()) {
                for (BookClassifyLv2Bean.MaleBean lv2Male : bookClassifyLv2Bean.getMale()) {
                    if (male.getName().equals(lv2Male.getMajor())) {
                        localBookClassifyList.add(new BookclassifyLocalBean.LocalBookClassifyBean(Constants.BOOK_TYPE_NAME, Constants.BOOK_TYPE_MALE, male.getName(), male.getBookCount(), lv2Male.getMins()));
                    }
                }
            }
            localBookClassifyList.add(new BookclassifyLocalBean.LocalBookClassifyBean(Constants.BOOK_TYPE_SIGN, "", "女生", 0, null));
            for (BookClassifyBean.FemaleBean female : bookClassifyBean.getFemale()) {
                for (BookClassifyLv2Bean.FemaleBean lv2Female : bookClassifyLv2Bean.getFemale()) {
                    if (female.getName().equals(lv2Female.getMajor())) {
                        localBookClassifyList.add(new BookclassifyLocalBean.LocalBookClassifyBean(Constants.BOOK_TYPE_NAME, Constants.BOOK_TYPE_FEMAL, female.getName(), female.getBookCount(), lv2Female.getMins()));
                    }
                }
            }
            localBookClassifyList.add(new BookclassifyLocalBean.LocalBookClassifyBean(Constants.BOOK_TYPE_SIGN, "", "漫画", 0, null));
            for (BookClassifyBean.PictureBean catroon : bookClassifyBean.getPicture()) {
                localBookClassifyList.add(new BookclassifyLocalBean.LocalBookClassifyBean(Constants.BOOK_TYPE_NAME, Constants.BOOK_TYPE_PICTURE, catroon.getName(), catroon.getBookCount(), null));
            }
            localBookClassifyList.add(new BookclassifyLocalBean.LocalBookClassifyBean(Constants.BOOK_TYPE_SIGN, "", "出版", 0, null));
            for (BookClassifyBean.PressBean publication : bookClassifyBean.getPress()) {
                localBookClassifyList.add(new BookclassifyLocalBean.LocalBookClassifyBean(Constants.BOOK_TYPE_NAME, Constants.BOOK_TYPE_PRESS, publication.getName(), publication.getBookCount(), null));
            }
            bookClassifyLocalData.setLocalBookclassifys(localBookClassifyList);
            bookClassifyLocalData.ok = true;
            return bookClassifyLocalData;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getBookClassifyList(HashMap map, BaseObserver observer) {
        mApiService.getBooksByClassify(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getAllRankingType(BaseObserver<LocalAllRankingTypeBean> observer) {
        mApiService.getAllRankingTypes().map(allRankTypeBean -> {
            LocalAllRankingTypeBean localAllRankingTypeBean = new LocalAllRankingTypeBean();
            localAllRankingTypeBean.ok = true;
            List<LocalAllRankingTypeBean.RankingBean> rankings = new ArrayList<>();
            rankings.add(new LocalAllRankingTypeBean.RankingBean(Constants.BOOK_TYPE_SIGN, "男生"));
            for (AllRankTypeBean.MaleBean male : allRankTypeBean.getMale()) {
                LocalAllRankingTypeBean.RankingBean ranking = new LocalAllRankingTypeBean.RankingBean(Constants.BOOK_TYPE_NAME, male.getTitle());
                ranking.set_id(male.get_id());
                ranking.setCollapse(male.isCollapse());
                ranking.setCover(male.getCover());
                ranking.setMonthRank(male.getMonthRank());
                ranking.setShortTitle(male.getShortTitle());
                ranking.setTotalRank(male.getTotalRank());
                rankings.add(ranking);
            }
            rankings.add(new LocalAllRankingTypeBean.RankingBean(Constants.BOOK_TYPE_SIGN, "女生"));
            for (AllRankTypeBean.FemaleBean female : allRankTypeBean.getFemale()) {
                LocalAllRankingTypeBean.RankingBean ranking = new LocalAllRankingTypeBean.RankingBean(Constants.BOOK_TYPE_NAME, female.getTitle());
                ranking.set_id(female.get_id());
                ranking.setCollapse(female.isCollapse());
                ranking.setCover(female.getCover());
                ranking.setMonthRank(female.getMonthRank());
                ranking.setShortTitle(female.getShortTitle());
                ranking.setTotalRank(female.getTotalRank());
                rankings.add(ranking);
            }
            localAllRankingTypeBean.setRanking(rankings);
            return localAllRankingTypeBean;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getBookDetail(String bookId, BaseObserver observer) {
        Observable.zip(mApiService.getBookDetail(bookId),
                mApiService.getRecommendBookList(bookId, 10 + ""),
                mApiService.getRecommendBook(bookId, 12 + ""),
                mApiService.getBookHotReview(bookId),
                (bookDetailBean, bookListBean, recommendBookBean, hotReviewBean) -> {
                    LocalBookdetailBean localBookdetail = new LocalBookdetailBean();
                    localBookdetail.ok = true;
                    localBookdetail.setBookDetail(bookDetailBean);
                    localBookdetail.setBookList(bookListBean);
                    localBookdetail.setHotReview(hotReviewBean);
                    localBookdetail.setRecommendBook(recommendBookBean);
                    return localBookdetail;
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getBookListByTag(HashMap map, BaseObserver<BookListTagBean> observer) {
        mApiService.getBookListByTag(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getDicussDetailRefresh(String discussId, HashMap<String, String> map, BaseObserver<CommentDetailBean> observer) {
        Observable.zip(mApiService.getReviewDetail(discussId),
                mApiService.getBestComment(discussId),
                mApiService.getBookDisscussionComments(discussId, map),
                (reviewDetailBean, bestComment, commentList) -> {
                    CommentDetailBean localDiscussDetail = new CommentDetailBean();
                    localDiscussDetail.ok = true;
                    localDiscussDetail.setCommentList(commentList);
                    localDiscussDetail.setBestComment(bestComment);
                    localDiscussDetail.setReviewDetail(reviewDetailBean);
                    return localDiscussDetail;
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getDiscussMore(String discussId, HashMap<String, String> map, BaseObserver<CommentListBean> observer) {
        mApiService.getBookDisscussionComments(discussId, map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getCommunityDiscuss(HashMap<String, String> map, BaseObserver<DiscussListBean> observer) {
        mApiService.getDisscussionListByBook(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void getCommunityComment(HashMap<String, String> map, BaseObserver<HotReviewBean> observer) {
        mApiService.getReviewListByBook(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }


}
