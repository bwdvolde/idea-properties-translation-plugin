package com.github.bwdvolde.ideapropertiesutil.services

import com.intellij.openapi.project.Project
import com.github.bwdvolde.ideapropertiesutil.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
