package com.github.bwdvolde.ideapropertiesutil

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import java.util.*

private val NOTIFICATION_GROUP = NotificationGroup(MyBundle.message("notifier.title"), NotificationDisplayType.BALLOON, true)

class PropertyTranslationNotifier {

    fun notify(project: Project, locales: Collection<Locale>) {
        val content = when (locales.isEmpty()) {
            true -> MyBundle.message("notifier.empty")
            else -> MyBundle.message("notifier.notEmpty", locales.joinToString())
        }
        val notification = NOTIFICATION_GROUP.createNotification(MyBundle.message("notifier.title"), content, NotificationType.INFORMATION)
        notification.notify(project)
    }
}
