package com.github.bwdvolde.ideapropertiesutil

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import java.util.*

private val NOTIFICATION_GROUP = NotificationGroup(MyBundle.message("notifier.title"), NotificationDisplayType.BALLOON, true)

class PropertyTranslationNotifier {

    fun notifySuccess(project: Project, propertyKey: String, locales: Collection<Locale>) {
        val content = when (locales.isEmpty()) {
            true -> MyBundle.message("notifier.empty", propertyKey)
            else -> MyBundle.message("notifier.notEmpty", propertyKey, locales.joinToString())
        }
        val notification = NOTIFICATION_GROUP.createNotification(MyBundle.message("notifier.title"), content, NotificationType.INFORMATION)
        notification.notify(project)
    }
}
