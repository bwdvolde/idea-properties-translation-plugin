package com.bwdvolde.ideapropertiestranslation

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import java.util.*

private val NOTIFICATION_GROUP = NotificationGroup(MyBundle.message("notifier.title"), NotificationDisplayType.BALLOON, true)

class PropertyTranslationNotifier {

    fun notifySuccess(project: Project, propertyKey: String, locales: Collection<Locale>) {
        val content = when (locales.isEmpty()) {
            true -> MyBundle.message("notifier.success.empty", propertyKey)
            else -> MyBundle.message("notifier.success.notEmpty", propertyKey, locales.joinToString())
        }
        notify(project, content, NotificationType.INFORMATION)
    }

    fun notifyFailure(project: Project, propertyKey: String) {
        val content = MyBundle.message("notifier.failure", propertyKey)
        notify(project, content, NotificationType.ERROR)
    }

    private fun notify(project: Project, content: String, notificationType: NotificationType) {
        val notification = NOTIFICATION_GROUP.createNotification(MyBundle.message("notifier.title"), content, notificationType)
        notification.notify(project)
    }
}
