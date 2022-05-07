from rest_framework.pagination import PageNumberPagination


class TwelvePagination(PageNumberPagination):
    page_size = 12


class TwentyPagination(PageNumberPagination):
    page_size = 20
