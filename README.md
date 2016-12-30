使用方法：

   DialogCitySelect dialogCitySelect = new DialogCitySelect(MainActivity.this);
                dialogCitySelect.setCallBack(new DialogCitySelect.CallBack() {
                    @Override
                    public void setData(DialogCitySelect.PCCData data) {

                        /**
                         * 返回的结果:省-市-县
                         *
                         */
                        content.setText(data.province + " - " + data.city + " - " + data.county);

                    }
                });
    dialogCitySelect.show();


    Add it in your root build.gradle at the end of repositories:

    	allprojects {
    		repositories {
    			...
    			maven { url 'https://jitpack.io' }
    		}
    	}
    Step 2. Add the dependency

    	dependencies {
    	        compile 'com.github.yylxy:AddressSelect:1.1'
    	}


###城市选择的效果-1
![](https://github.com/yylxy/AddressSelect/blob/master/picture.png)

###动图的展示-2
![](https://github.com/yylxy/AddressSelect/blob/master/jdfw.gif)
