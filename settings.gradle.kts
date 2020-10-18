rootProject.name = "kached"

include(":core")

// Serializers
include(":serializer")
include(":serializer:gson-serializer")

// Storage
include(":storage")
include(":storage:simple-memory-storage")

// Demos
include(":demo")
include(":demo:gson-memory-demo")
