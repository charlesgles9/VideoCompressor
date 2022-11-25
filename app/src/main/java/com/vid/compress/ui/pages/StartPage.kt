package com.vid.compress.ui.pages

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.vid.compress.R
import com.vid.compress.permisions.PermissionHelper
import com.vid.compress.ui.theme.LightBlue
import kotlinx.coroutines.launch


@Composable
fun welcomeView(){

    Box(modifier = Modifier.fillMaxSize()) {

        Text(text = "Welcome!", fontSize = 18.sp,
            modifier = Modifier
                .background(color = LightBlue, shape = Shapes().medium)
                .padding(15.dp)
                .align(Alignment.Center), color = Color.White)

    }
}

@Composable
fun getStarted(){
    Column(modifier = Modifier
        .fillMaxSize()
        .clickable {

        }) {
        Box(modifier = Modifier.fillMaxSize()) {


            Text(text = "Get Started!", fontSize = 18.sp,
                modifier = Modifier
                    .background(color = LightBlue, shape = Shapes().medium)
                    .padding(15.dp)
                    .align(Alignment.Center),
                    color = Color.White)


        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun StartView(context:Activity){
    val coroutineScope= rememberCoroutineScope()
    val pageState= rememberPagerState()
    val constraint= ConstraintSet {
    val pagerLayout = createRefFor("pagerLayout")
    val pagerTabs = createRefFor("pagerTabs")
    constrain(pagerLayout) {
       centerTo(parent)
    }
    constrain(pagerTabs) {
        bottom.linkTo(parent.bottom)
    }

}

    ConstraintLayout(modifier = Modifier.fillMaxSize(), constraintSet = constraint) {

        HorizontalPager(count = 2, state = pageState,
            modifier = Modifier.layoutId("pagerLayout")) {
                currentPage->

            if(currentPage==0) {
                welcomeView()
               }else{

                    getStarted()
                    // normal storage permission
                    if(PermissionHelper.checkStoragePermissionDenied(context))
                        PermissionHelper.grantStorageReadWrite(context)
                }

        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .layoutId("pagerTabs")) {

            Row(modifier = Modifier
                .width(90.dp)
                .align(Alignment.BottomCenter)) {
                IconToggleButton(checked = (pageState.currentPage == 0), enabled = false, onCheckedChange = {

                    }, modifier = Modifier.padding(start = 10.dp).width(20.dp)) {

                    Image(painter = painterResource(id = if(pageState.currentPage==0)
                        R.drawable.ic_circle_checked else R.drawable.ic_circle
                        ), contentDescription = "0")
                }

                IconToggleButton(checked = (pageState.currentPage == 1), enabled = false, onCheckedChange = {


                }, modifier = Modifier.padding(start = 10.dp).width(20.dp)) {

                    Image(painter = painterResource(id = if(pageState.currentPage==1)
                        R.drawable.ic_circle_checked else R.drawable.ic_circle
                    ), contentDescription = "1")
                }

            }

                Text(text = "NEXT", fontSize = 18.sp,
                    modifier = Modifier
                        .padding(5.dp).background(color= LightBlue, shape = Shapes().medium)
                        .clickable {
                            if (pageState.currentPage < 1)
                                coroutineScope.launch {
                                    pageState.animateScrollToPage(pageState.currentPage + 1)
                                }

                            if(pageState.currentPage==1){

                                if(!PermissionHelper.checkStoragePermissionDenied(context)){
                                    //go to main Activity
                                    val intent: Intent = Intent(context,
                                        Class.forName("com.vid.compress.MainActivity"))
                                    context.startActivity(intent)
                                    context.finish()
                                } else{
                                    Toast.makeText(context,"Please grant storage access!",Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        .padding(5.dp)
                        .align(Alignment.BottomEnd), color = Color.White)

        }

    }

}
