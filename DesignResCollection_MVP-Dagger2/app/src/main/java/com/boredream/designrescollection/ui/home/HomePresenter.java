package com.boredream.designrescollection.ui.home;

import com.boredream.bdcodehelper.entity.ListResponse;
import com.boredream.bdcodehelper.net.ObservableDecorator;
import com.boredream.bdcodehelper.utils.ErrorInfoUtils;
import com.boredream.designrescollection.constants.CommonConstants;
import com.boredream.designrescollection.entity.DesignRes;
import com.boredream.designrescollection.net.ApiService;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class HomePresenter implements HomeContract.Presenter {

    private final HomeContract.View view;
    private final ApiService service;
    public List<DesignRes> datas;

    public HomePresenter(HomeContract.View view, ApiService service) {
        this.view = view;
        this.service = service;
        this.view.setPresenter(this);
    }

    @Override
    public void pullToLoadList() {
        loadData(1);
    }

    @Override
    public void loadList(final int page) {
        if(page == 1) {
            view.showProgress();
        }

        loadData(page);
    }

    private void loadData(final int page) {
        Observable<ListResponse<DesignRes>> observable = service.getDesignRes(
                CommonConstants.COUNT_OF_PAGE, (page - 1) * CommonConstants.COUNT_OF_PAGE);
        ObservableDecorator.decorate(observable).subscribe(
                new Subscriber<ListResponse<DesignRes>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!view.isActive()) {
                            return;
                        }
                        view.dismissProgress();

                        String error = ErrorInfoUtils.parseHttpErrorInfo(e);
                        view.showTip(error);
                    }

                    @Override
                    public void onNext(ListResponse<DesignRes> response) {
                        if (!view.isActive()) {
                            return;
                        }
                        view.dismissProgress();

                        datas = response.getResults();
                        view.loadListSuccess(page, datas);
                    }
                });
    }

}
