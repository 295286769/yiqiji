/***
 * Copyright (c) 2008-2009 CommonsWare, LLC
 * Portions (c) 2009 Google, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yiqiji.money.modules.common.adapter;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that merges multiple child adapters and views into a single
 * contiguous whole.
 *
 * Adapters used as pieces within MergeAdapter must have view type IDs
 * monotonically increasing from 0. Ideally, adapters also have distinct ranges
 * for their row ids, as returned by getItemId().
 *
 */
public class MergeAdapter extends BaseAdapter {

    private ArrayList<ListAdapter> pieces = new ArrayList<ListAdapter>();// 保存adapter的集合

    /**
     * 构造函数
     */
    public MergeAdapter() {
        super();
    }

    /**
     * 添加一个适配器
     */
    public void addAdapter(ListAdapter adapter) {
        pieces.add(adapter);// 添加到pieces集合中
        adapter.registerDataSetObserver(new CascadeDataSetObserver());// 注册一个观察者
    }

    /**
     * Adds a new View to the roster of things to appear in the aggregate list.
     *
     * @param view
     *            Single view to add
     */
    public void addView(View view) {
        addView(view, false);
    }

    /**
     * Adds a new View to the roster of things to appear in the aggregate list.
     *
     * @param view
     *            Single view to add
     * @param enabled
     *            false if views are disabled, true if enabled
     */
    public void addView(View view, boolean enabled) {
        ArrayList<View> list = new ArrayList<View>(1);// 创建只有一个元素的ArrayList<View>
        list.add(view);
        addViews(list, enabled);
    }

    /**
     * Adds a list of views to the roster of things to appear in the aggregate
     * list.
     *
     * @param views
     *            List of views to add
     */
    public void addViews(List<View> views) {
        addViews(views, false);
    }

    /**
     * Adds a list of views to the roster of things to appear in the aggregate
     * list.
     *
     * @param views
     *            List of views to add
     * @param enabled
     *            false if views are disabled, true if enabled
     */
    public void addViews(List<View> views, boolean enabled) {
        if (enabled) {
            addAdapter(new EnabledSackAdapter(views));
        } else {
            addAdapter(new SackOfViewsAdapter(views));
        }
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position
     *            Position of the item whose data we want
     */
    @Override
    public Object getItem(int position) {
        for (ListAdapter piece : pieces) {
            int size = piece.getCount();

            if (position < size) {
                return (piece.getItem(position));
            }

            position -= size;
        }

        return (null);
    }

    /**
     * Get the adapter associated with the specified position in the data set.
     *
     * @param position
     *            Position of the item whose adapter we want
     */
    public ListAdapter getAdapter(int position) {
        for (ListAdapter piece : pieces) {
            int size = piece.getCount();

            if (position < size) {
                return (piece);
            }

            position -= size;
        }

        return (null);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     */
    @Override
    public int getCount() {
        int total = 0;

        for (ListAdapter piece : pieces) {
            total += piece.getCount();
        }

        return (total);
    }

    /**
     * Returns the number of types of Views that will be created by getView().
     */
    @Override
    public int getViewTypeCount() {
        int total = 0;

        for (ListAdapter piece : pieces) {
            total += piece.getViewTypeCount();
        }

        return (Math.max(total, 1)); // needed for setListAdapter() before
        // content add'
    }

    /**
     * Get the type of View that will be created by getView() for the specified
     * item.
     *
     * @param position
     *            Position of the item whose data we want
     */
    @Override
    public int getItemViewType(int position) {
        int typeOffset = 0;
        int result = -1;

        for (ListAdapter piece : pieces) {
            int size = piece.getCount();

            if (position < size) {
                result = typeOffset + piece.getItemViewType(position);
                break;
            }

            position -= size;
            typeOffset += piece.getViewTypeCount();
        }

        return (result);
    }

    /**
     * Are all items in this ListAdapter enabled? If yes it means all items are
     * selectable and clickable.
     */
    @Override
    public boolean areAllItemsEnabled() {
        return (false);
    }

    /**
     * Returns true if the item at the specified position is not a separator.
     *
     * @param position
     *            Position of the item whose data we want
     */
    @Override
    public boolean isEnabled(int position) {
        for (ListAdapter piece : pieces) {
            int size = piece.getCount();

            if (position < size) {
                return (piece.isEnabled(position));
            }

            position -= size;
        }

        return (false);
    }

    /**
     * Get a View that displays the data at the specified position in the data
     * set.
     *
     * @param position
     *            Position of the item whose data we want
     * @param convertView
     *            View to recycle, if not null
     * @param parent
     *            ViewGroup containing the returned View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        for (ListAdapter piece : pieces) {
            int size = piece.getCount();

            if (position < size) {
                return (piece.getView(position, convertView, parent));
            }

            position -= size;
        }

        return (null);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position
     *            Position of the item whose data we want
     */
    @Override
    public long getItemId(int position) {
        for (ListAdapter piece : pieces) {
            int size = piece.getCount();

            if (position < size) {
                return (piece.getItemId(position));
            }

            position -= size;
        }

        return (-1);
    }

    /**
     * 可以使用的适配器
     */
    private static class EnabledSackAdapter extends SackOfViewsAdapter {

        /**
         * 构造函数
         */
        public EnabledSackAdapter(List<View> views) {
            super(views);
        }

        // 重写这2个方法，使得每一项都可以用
        @Override
        public boolean areAllItemsEnabled() {
            return (true);
        }

        @Override
        public boolean isEnabled(int position) {
            return (true);
        }
    }

    /**
     * 观察者，当数据集改变或者无效了，将会被回调
     */
    private class CascadeDataSetObserver extends DataSetObserver {

        // 当整个数据集发生改变后回调
        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }

        // 当整个数据集无效后回调
        @Override
        public void onInvalidated() {
            notifyDataSetInvalidated();
        }
    }
}