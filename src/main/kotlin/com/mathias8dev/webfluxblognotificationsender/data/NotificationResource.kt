package com.mathias8dev.webfluxblognotificationsender.data

enum class NotificationResource(
    val mailTemplatePath: String,
    val smsTemplatePath: String,
    val subject: String,
    val sender: String = Strings.DEFAULT_SENDER
) {

    ACCOUNT_CREATED(
        "templates/html/notification.account.created.html",
        "templates/text/notification.account.created.txt",
        Strings.ACCOUNT_CREATED,
    ),

    PASSWORD_RESET(
        "templates/html/notification.password.reset.html",
        "templates/text/notification.password.reset.txt",
        Strings.PASSWORD_RESET,
    ),

    PASSWORD_RESET_REQUESTED(
        "templates/html/notification.password.reset-requested.html",
        "templates/text/notification.password.reset-requested.txt",
        Strings.PASSWORD_RESET_REQUESTED,
    ),

    ACCOUNT_CONFIRM_EMAIL(
        "templates/html/notification.account.confirm-email.html",
        "templates/text/notification.account.confirm-email.txt",
        Strings.ACCOUNT_CONFIRM_EMAIL,
    ),
}