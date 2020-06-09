val arrowVersion: String by project.ext

dependencies {
    implementation("io.arrow-kt:arrow-mtl:$arrowVersion")
    implementation("io.arrow-kt:arrow-fx:$arrowVersion")
    implementation("io.arrow-kt:arrow-fx-reactor:$arrowVersion")
    implementation("io.arrow-kt:arrow-fx-rx2:$arrowVersion")
}