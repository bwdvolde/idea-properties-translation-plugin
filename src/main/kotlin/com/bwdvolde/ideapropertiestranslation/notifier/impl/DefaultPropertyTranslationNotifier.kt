package com.bwdvolde.ideapropertiestranslation.notifier.impl

import com.bwdvolde.ideapropertiestranslation.MyBundle
import com.bwdvolde.ideapropertiestranslation.notifier.PropertyTranslationNotifier
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import java.util.*

private val NOTIFICATION_GROUP = NotificationGroup(MyBundle.message("notifier.title"), NotificationDisplayType.BALLOON, true)

class DefaultPropertyTranslationNotifier : PropertyTranslationNotifier {

    override fun notifySuccess(project: Project, locales: Collection<Locale>) {
        val content = when (locales.isEmpty()) {
            true -> MyBundle.message("notifier.success.empty")
            else -> MyBundle.message("notifier.success.notEmpty", locales.joinToString())
        }
        notify(project, content, NotificationType.INFORMATION)
    }

    override fun notifyFailure(project: Project) {
        val content = MyBundle.message("notifier.failure")
        notify(project, content, NotificationType.ERROR)
    }

    private fun notify(project: Project, content: String, notificationType: NotificationType) {
        val notification = NOTIFICATION_GROUP.createNotification(MyBundle.message("notifier.title"), content, notificationType)
        notification.notify(project)
    }
}
