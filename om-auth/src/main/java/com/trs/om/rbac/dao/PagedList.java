/**
 * 
 */
package com.trs.om.rbac.dao;

import java.util.AbstractList;
import java.util.List;

/**
 * @author Administrator
 *
 */
public class PagedList extends AbstractList {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 当前页记录列表
	 */
    private List pageItems;
    /**
     * 当前页码
     */
    private int pageIndex;
    /**
     * 每页记录数
     */
    private int pageSize;
    /**
     * 总记录数
     */
    private int totalItemCount;

    /**
     * @param pageItems
     * @param pageNumber
     * @param itemsPerPage
     * @param totalItemCount
     */
    public PagedList(List pageItems, int pageIndex, int pageSize, int totalItemCount) {
    	this.pageItems = pageItems;
    	this.pageIndex = pageIndex;
    	this.pageSize = pageSize;
    	this.totalItemCount = totalItemCount;
    }

    /**
     * @see java.util.AbstractList#get(int)
     */
    public Object get(int index) {
        return this.pageItems.get(index);
    }

    /**
     * @see java.util.AbstractCollection#size()
     */
    public int size() {
        return this.pageItems.size();
    }

    /**
     * @return the list of items for this page
     */
    public List getPageItems() {
        return pageItems;
    }

    /**
     * @return total count of items
     */
    public int getTotalItemCount() {
        return totalItemCount;
    }

    /**
     * @return total count of pages
     */
    public int getTotalPageCount() {
        return (int) Math.ceil((double) getTotalItemCount() / getPageSize());
    }

    /**
     * @return true if this is the first page
     */
    public boolean isFirstPage() {
        return isFirstPage(getPageIndex());
    }

    /**
     * @return true if this is the last page
     */
    public boolean isLastPage() {
        return isLastPage(getPageIndex());
    }

    /**
     * @param page
     * @return true if the page is the first page
     */
    public boolean isFirstPage(int page) {
        return page <= 1;
    }

    /**
     * @param page
     * @return true if the page is the last page
     */
    public boolean isLastPage(int page) {
        return page >= getTotalPageCount();
    }

    /**
     * @see java.util.AbstractCollection#toString()
     */
    public String toString() {
    	return new StringBuffer(256).append(this).append(getPageItems()).append(getPageIndex()).append(getPageSize()).append(getTotalItemCount()).toString();
    }

	/**
	 * @return the pageIndex
	 */
	public int getPageIndex() {
		return pageIndex;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}


}
