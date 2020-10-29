package io.kached.serializer

import com.google.gson.Gson
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import kotlin.reflect.typeOf

@ExperimentalCoroutinesApi
class GsonSerializerTest {

    class Person(val id: String, val name: String) {
        companion object {
            val INSTANCE = Person("1", "Gustavo")
            val JSON = "{\"id\": \"1\", \"name\": \"Gustavo\"}"
            val KCLASS = Person::class
            val KTYPE = typeOf<Person>()
        }
    }

    private val gson = mockk<Gson>()
    private val subject = GsonSerializer(gson)

    @Before
    fun setUp() {
        every { gson.toJson(Person.INSTANCE) } returns Person.JSON
        every { gson.fromJson(Person.JSON, Person::class.java) } returns Person.INSTANCE
    }

    @Test
    fun serialize_callsGsonToJson() = runBlockingTest {
        subject.serialize(Person.INSTANCE, Person.KCLASS, Person.KTYPE)

        verify(exactly = 1) { gson.toJson(Person.INSTANCE) }
    }

    @Test
    fun deserialize_callsGsonFromJson() = runBlockingTest {
        subject.deserialize(Person.JSON, Person.KCLASS, Person.KTYPE)

        verify(exactly = 1) { gson.fromJson(Person.JSON, Person.KCLASS.java) }
    }
}
