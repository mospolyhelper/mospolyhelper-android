package com.mospolytech.mospolyhelper.features.ui.schedule.group_info

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.data.schedule.api.GroupInfoApi
import com.mospolytech.mospolyhelper.data.schedule.remote.GroupInfoRemoteDataSource
import com.mospolytech.mospolyhelper.databinding.FragmentGroupInfoBinding
import com.mospolytech.mospolyhelper.features.ui.account.students.adapter.StudentsAdapter
import com.mospolytech.mospolyhelper.utils.onSuccess
import io.ktor.client.*
import kotlinx.android.synthetic.main.fragment_account_students.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking

class GroupInfoFragment : Fragment(R.layout.fragment_group_info) {
    private val viewBinding by viewBinding(FragmentGroupInfoBinding::bind)
    private val args: GroupInfoFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding) {
            runBlocking {

                GroupInfoRemoteDataSource(GroupInfoApi(HttpClient())).get().onSuccess {
                    var isShow = true
                    var scrollRange = -1
                    appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
                        if (scrollRange == -1){
                            scrollRange = barLayout?.totalScrollRange!!
                        }
                        if (scrollRange + verticalOffset * 1.1f <= 0){
                            toolbar.title = "Группа ${args.group}"
                            isShow = true
                        } else if (isShow){
                            toolbar.title = " " //careful there should a space between double quote otherwise it wont work
                            isShow = false
                        }
                    })
                    val groupInfo = it.content[args.group]!!
                    val levelCode = """\d+\.(\d+)\.[\d\.]+""".toRegex()
                        .find(groupInfo.directionCode)
                        ?.groupValues?.elementAtOrNull(1) ?: "00"
                    val level = when (levelCode) {
                        "02" -> "СПО"
                        "03" -> "Бакалавры"
                        "04" -> "Магистры"
                        "05" -> "Специалисты"
                        "06" -> "Аспиранты"
                        else -> ""
                    }
                    val form = groupInfo.educationForm

                    //${groupInfo.directionCode}
                    textviewTitle.text = "Группа ${args.group}"
                    textviewCourse.text = "$level ${groupInfo.course}-го курса"
                    textviewDirection.text = "${groupInfo.directionCode} ${groupInfo.direction}"
                    textviewSpecialization.text = "${groupInfo.specialization.capitalize()}"
                    val a = "Форма: <b>$form</b>         В группе: <b>${groupInfo.count} чел.</b>".replace(" ", "&nbsp;")
                    val q = HtmlCompat.fromHtml(a, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    textviewCount.text = q
                    viewpager.isUserInputEnabled = false
                    viewpager.adapter = GroupInfoAdapter(this@GroupInfoFragment)
                    TabLayoutMediator(tabLayout, viewpager) { tab, position ->
                        tab.text = when (position) {
                            0 -> "Расписание"
                            1 -> "Студенты"
                            else -> ""
                        }
                    }.attach()

                }
            }

        }
    }
}