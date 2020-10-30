package io.kached.serializer

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import org.junit.Assert
import org.junit.Test
import kotlin.reflect.typeOf

@ExperimentalCoroutinesApi
@InternalSerializationApi
class KotlinxJsonSerializerTest {

    private val serializer = KotlinxJsonSerializer()

    @Serializable
    class Person(val id: String, val name: String) {
        companion object {
            val INSTANCE = Person("1", "Gustavo")
            val JSON = "{\"id\":\"1\",\"name\":\"Gustavo\"}"
            val KCLASS = Person::class
            val KTYPE = typeOf<Person>()
        }
    }

    @Test
    fun serializeWithKotlinxJsonSerializer() = runBlockingTest {
        val str = serializer.serialize(Person.INSTANCE, Person.KCLASS, Person.KTYPE)
        Assert.assertEquals(Person.JSON, str)
    }

    @Test
    fun deserializeWithKotlinxJsonSerializer() = runBlockingTest {
        val result = serializer.deserialize(Person.JSON, Person.KCLASS, Person.KTYPE)
        Assert.assertEquals(Person.INSTANCE.id, result.id)
        Assert.assertEquals(Person.INSTANCE.name, result.name)
    }
}
