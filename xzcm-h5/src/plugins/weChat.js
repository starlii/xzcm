const wx = require('weixin-js-sdk')

const  wxPay = (args) => {

  // 这里要注意服务端返回的package字段多个s字符 timestamp字段不对 不能直接使用服务端的对象
  // appId : "wx08ff7622c5cf726a"
  // nonceStr : "Od24vgZJH2rOil4uQqsERkx29OuaVS3w"
  // packages : "prepay_id=wx20170623115948ec6c51115e0839532978"
  // paySign : "DF45387FCF2FB360BBCD642A1E93341E"
  // signType : "MD5"
  // timestamp : "1498190115"

  args = {
    appId: args.appId,
    nonceStr: args.nonceStr,
    package: args.packages || args.package,
    paySign: args.paySign,
    signType: args.signType,
    timeStamp: args.timestamp || args.timeStamp
  }

  return new Promise(resolve => {
    let result = {success: false, data: null }
    if (typeof WeixinJSBridge == "undefined") {
      document.addEventListener('WeixinJSBridgeReady', function() {
        WeixinJSBridge.invoke('getBrandWCPayRequest', args, function (res) {
          result.data = res
          if(res.err_msg == "get_brand_wcpay_request:ok" ) {
            result.success = true
          } else {
            result.success = false
          }
          resolve(result)
        })
      }, false)

      // 超时
      setTimeout(() => {
        if (typeof WeixinJSBridge == "undefined") {
          result.success = false
          resolve(result)
        }
      }, 1000 * 10)
    }
    else {
      WeixinJSBridge.invoke('getBrandWCPayRequest', args, function (res) {
        result.data = res
        if(res.err_msg == "get_brand_wcpay_request:ok" ) {
          result.success = true
        } else {
          result.success = false
        }
        resolve(result)
      })
    }
  })
}


const weChat = {
  install (Vue) {
    Vue.prototype.$wx = wx
    Vue.$wx = wx

    Vue.prototype.$wxPay = wxPay
    Vue.$wxPay = wxPay
  },
  $wx: wx
}

export default weChat
