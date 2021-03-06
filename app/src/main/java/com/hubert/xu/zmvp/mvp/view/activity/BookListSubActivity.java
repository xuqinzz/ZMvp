package com.hubert.xu.zmvp.mvp.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hubert.xu.zmvp.R;
import com.hubert.xu.zmvp.base.BaseActivity;
import com.hubert.xu.zmvp.mvp.contract.BookClassifyListContract;
import com.hubert.xu.zmvp.mvp.model.entity.BookClassifyListBean;
import com.hubert.xu.zmvp.mvp.model.entity.BookclassifyLocalBean;
import com.hubert.xu.zmvp.mvp.presenter.BookClassifyListPresenter;
import com.hubert.xu.zmvp.mvp.view.adapter.BookClassifyListAdapter;

import java.util.List;

import butterknife.BindView;

public class BookListSubActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, BookClassifyListContract.View<BookClassifyListBean>, BaseQuickAdapter.RequestLoadMoreListener {

    private static final String INTENT_BOOK_LIST_DATA = "intent_book_list_data";
    private static final String INTENT_BOOK_LIST_BUNDLE = "intent_book_list_bundle";
    @BindView(R.id.rv_book_list_sub)
    RecyclerView mRvBookListSub;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeLayout;
    private BookclassifyLocalBean.LocalBookClassifyBean mData;
    private BookClassifyListAdapter mAdapter;
    private BookClassifyListPresenter mPresenter;
    private int start;
    private List<BookClassifyListBean.BooksBean> mBooks;


    public static void startActivity(Context context, BookclassifyLocalBean.LocalBookClassifyBean data) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_BOOK_LIST_DATA, data);
        context.startActivity(new Intent(context, BookListSubActivity.class)
                .putExtra(INTENT_BOOK_LIST_BUNDLE, bundle));
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_book_list_classify_sub;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mData = (BookclassifyLocalBean.LocalBookClassifyBean) getIntent().getBundleExtra(INTENT_BOOK_LIST_BUNDLE).get(INTENT_BOOK_LIST_DATA);
        mTvTitle.setText(mData.getName());
        mRvBookListSub.setLayoutManager(new LinearLayoutManager(this));
        mPresenter = new BookClassifyListPresenter(this);
        mAdapter = new BookClassifyListAdapter(R.layout.item_book, mBooks);
        mRvBookListSub.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(this, mRvBookListSub);
        mSwipeLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener((adapter, view, position) -> BookDetailActivity.startActivity(BookListSubActivity.this, mBooks.get(position).getTitle(), mBooks.get(position).get_id()));
        onRefresh();
    }

    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(true);
        mRvBookListSub.scrollToPosition(0);
        start = 0;
        mPresenter.getData(start, mData.getType(), "", mData.getName(), "");
    }


    @Override
    public void onLoadMoreRequested() {
        mPresenter.getData(start, mData.getType(), "", mData.getName(), "");
    }

    @Override
    public void setData(BookClassifyListBean data, boolean isRefresh) {
        mSwipeLayout.setRefreshing(false);
        mAdapter.loadMoreComplete();
        if (isRefresh) {
            mBooks = data.getBooks();
        } else {
            mBooks.addAll(data.getBooks());
        }
        mAdapter.setNewData(mBooks);
        start = start + data.getBooks().size();
        mAdapter.setEnableLoadMore(data.getBooks() != null && data.getBooks().size() >= 20);
    }

    @Override
    public void showError() {
        mSwipeLayout.setRefreshing(false);
        mAdapter.loadMoreFail();
    }

    @Override
    public void complete() {

    }
}
