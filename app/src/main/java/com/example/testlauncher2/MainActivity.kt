package com.example.testlauncher2

import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testlauncher2.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

         /**Приведенная инструкция возвращает список всех приложений,
         доступных для запуска пользователем, в виде [ResolveInfo].
         Этот класс содержит множество информации, которая нам не нужна,
         поэтому мы создаем наш собственный класс данных под названием
         [AppBlock] для облегчения обработки данных.*/

        val resolvedApplist:List<ResolveInfo> = packageManager
            .queryIntentActivities(Intent(Intent.ACTION_MAIN,null)
                .addCategory(Intent.CATEGORY_LAUNCHER),0)



        /**поверяем приложения. Если пакет приложения не равен лончеру
        который мы создали, то обавляем объект [AppBlock]
        в [appList]*/

        val appList = ArrayList<AppBlock>()

        for (ri in resolvedApplist) {
            if(ri.activityInfo.packageName!=this.packageName) {
                val app = AppBlock(
                    ri.loadLabel(packageManager).toString(), // получаем название
                    ri.activityInfo.loadIcon(packageManager), // получаем иконку
                    ri.activityInfo.packageName // получаем имя пакета приложения
                )
                appList.add(app)
            }
        }


        // Затем Adapter инициализируется, и список приложений передается ему с помощью функции passAppList().
        // сортируем приложения в алфовитном порядке. Делаем это всё не в основном покоте,
        // ибо это ресурса затратный процесс.

        mainBinding.appRV.adapter = Adapter(this).also {
            CoroutineScope(Dispatchers.IO).launch {
                it.passAppList(appList.sortedBy { it.appName })
            }
        }
    }

}

