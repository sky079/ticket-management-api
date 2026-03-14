package com.ticket.ticketmanagement.dto;

import java.util.List;

public class PageResponse<T> {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLastPage;

    public PageResponse(List<T> content, int pageNumber, int pageSize,
                        long totalElements, int totalPages, boolean isLastPage) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.isLastPage = isLastPage;
    }

    public List<T> getContent() { return content; }
    public int getPageNumber() { return pageNumber; }
    public int getPageSize() { return pageSize; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isLastPage() { return isLastPage; }

    public void setContent(List<T> content) { this.content = content; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public void setLastPage(boolean lastPage) { isLastPage = lastPage; }
}