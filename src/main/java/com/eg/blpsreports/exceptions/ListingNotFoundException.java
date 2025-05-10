package com.eg.blpsreports.exceptions;

public class ListingNotFoundException extends CustomException {
    public ListingNotFoundException(Long listingId) {
        super("Объявления таким id={" + listingId + "} не найдено");
    }
}
