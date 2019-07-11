/**
 * Created By shli on 2018/6/29
 */
import _ from 'lodash'

export const formatDate = (value, format) => {
  if (!value) return ''
  value = value.toString()
  return formatDate(value, format)
  function formatDate(value, format) {
    var newServerTime = new Date(value.replace('-', '/').replace('-', '/'))
    if (format === undefined) {
      format = 'MM/dd/yyyy hh:mm:ss'
    }
    var utc = newServerTime.getTime()
    // var tempDate = utc + (3600000 * (new Date().getTimezoneOffset() / (-60)))
    var tempDate = utc + (new Date().getTimezoneOffset() / (-60))
    var newDate = new Date(tempDate)
    newServerTime = newDate
    var o = {
      'M+': newServerTime.getMonth() + 1, // month
      'd+': newServerTime.getDate(), // day
      'h+': newServerTime.getHours(), // house
      'm+': newServerTime.getMinutes(), // min
      's+': newServerTime.getSeconds(), // second
      'q+': Math.floor((newServerTime.getMonth() + 3) / 3), // Quarterly
      'S': newServerTime.getMilliseconds() // millisecond
    }

    if (/(y+)/.test(format)) { format = format.replace(RegExp.$1, (newServerTime.getFullYear() + '').substr(4 - RegExp.$1.length)) }
    for (var k in o) {
      if (new RegExp('(' + k + ')').test(format)) { format = format.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length))) }
    }
    if (value != null) {
      return format
    } else {
      return ''
    }
  }
}
const fn = (val) => {
  return val < 10 ? '0' + val : val
}
const formatDuring = (mss) => {
  var days = fn(parseInt(mss / (1000 * 60 * 60 * 24)))
  var hours = fn(parseInt((mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)))
  var minutes = fn(parseInt((mss % (1000 * 60 * 60)) / (1000 * 60)))
  var seconds = fn(parseInt((mss % (1000 * 60)) / 1000))
  return days + ' 天 ' + hours + ' 小时 ' + minutes + ' 分钟 ' + seconds + ' 秒 '
}
const filters = {
  formatDate,
  formatDuring,
  formatDateInWeek: (value, format) => {
    if (!value) return ''
    value = value.toString()
    return formatDateInWeek(value, format)
    function formatDateInWeek(value, format) {
      var newServerTime = new Date(value.replace('-', '/').replace('-', '/'))
      if (format === undefined) {
        format = 'MM yyyy'
      }
      var utc = newServerTime.getTime()
      var tempDate = utc + (3600000 * (new Date().getTimezoneOffset() / (-60)))
      var newDate = new Date(tempDate)
      newServerTime = newDate
      var o = {
        'M+': newServerTime.toDateString().split(' ')[1] // month
      }
      if (/(y+)/.test(format)) { format = format.replace(RegExp.$1, (newServerTime.getFullYear() + '').substr(4 - RegExp.$1.length)) }
      for (var k in o) {
        if (new RegExp('(' + k + ')').test(format)) { format = format.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (('000' + o[k]).substr(('' + o[k]).length))) }
      }
      if (value != null) {
        return format
      } else {
        return ''
      }
    }
  },
  currency: (value, places, symbol, thousand, decimal) => {
    if (!value) { return '' }
    value = value.toString()
    return formatMoney(value, symbol, places, thousand, decimal)
    function formatMoney(number, symbol, places, thousand, decimal) {
      number = number || 0
      places = !isNaN(places = Math.abs(places)) ? places : 2
      symbol = symbol !== undefined ? symbol : '$'
      thousand = thousand || ','
      decimal = decimal || '.'
      const negative = number < 0 ? '-' : ''
      const i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + ''
      let j = (j = i.length) > 3 ? j % 3 : 0
      return symbol + negative + (j ? i.substr(0, j) + thousand : '') + i.substr(j).replace(/(\d{3})(?=\d)/g, '$1' + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : '')
    }
  },
  formatFilters: (value) => {
    if (!value) return ''
    value = JSON.parse(value)
    return formatFilters(value)
    function formatFilters(value) {
      const filters = []
      value.key ? filters.push('Keyword') : null
      value.categoryId ? filters.push('Category') : null
      value.brandId ? filters.push('Brand') : null
      value.seriesId ? filters.push('Series') : null
      value.deviceIds ? filters.push('Device') : null
      value.startMarkup || value.endMarkup ? filters.push('Markup') : null
      value.startMargin || value.endMargin ? filters.push('Margin') : null
      value.startCostPrice || value.endCostPrice ? filters.push('Cost Price') : null
      value.startCreateDate || value.endCreateDate ? filters.push('Create Date') : null
      value.startReleasedDate || value.endReleasedDate ? filters.push('Released Date') : null
      value.startStockQty || value.endStockQty ? filters.push('IS QTY') : null
      value.startOos || value.endOos || value.startOosDate || value.endOosDate ? filters.push('OOSDS') : null
      value.startOost || value.endOost || value.startOostDate || value.endOostDate ? filters.push('OOST') : null
      value.startBisd || value.endBisd || value.startBisdDate || value.endBisdDate ? filters.push('BISD') : null
      value.startPoFrequency || value.endPoFrequency || value.startPoFrequencyDate || value.endPoFrequencyDate ? filters.push('PO Frequency') : null
      value.startSoldQty || value.endSoldQty || value.startSoldQtyDate || value.endSoldQtyDate ? filters.push('Sold QTY') : null
      value.startTransaction || value.endTransaction || value.startTransactionDate || value.endTransactionDate ? filters.push('Transaction #') : null
      value.reOrderStartNum || value.reOrderEndNum || value.reOrderEqualNum === 0 || value.reOrderLeNum === 0 ? filters.push('Re-Order Point') : null
      const filtersArray = filters.filter(function(element, index, self) {
        return self.indexOf(element) === index
      })
      return filtersArray.join(', ')
    }
  },
  formatDateToMonthAndYear: (value) => {
    const firstFormat = formatDate(value, 'MM/yyyy')
    const firstFormatArray = firstFormat.split('/')
    if (firstFormatArray[0] === '01') {
      return 'Jan ' + firstFormatArray[1]
    } else if (firstFormatArray[0] === '02') {
      return 'Feb ' + firstFormatArray[1]
    } else if (firstFormatArray[0] === '03') {
      return 'Mar ' + firstFormatArray[1]
    } else if (firstFormatArray[0] === '04') {
      return 'Apr ' + firstFormatArray[1]
    } else if (firstFormatArray[0] === '05') {
      return 'May ' + firstFormatArray[1]
    } else if (firstFormatArray[0] === '06') {
      return 'Jun ' + firstFormatArray[1]
    } else if (firstFormatArray[0] === '07') {
      return 'Jul ' + firstFormatArray[1]
    } else if (firstFormatArray[0] === '08') {
      return 'Aug ' + firstFormatArray[1]
    } else if (firstFormatArray[0] === '09') {
      return 'Sep ' + firstFormatArray[1]
    } else if (firstFormatArray[0] === '10') {
      return 'Oct ' + firstFormatArray[1]
    } else if (firstFormatArray[0] === '11') {
      return 'Nov ' + firstFormatArray[1]
    } else if (firstFormatArray[0] === '12') {
      return 'Dec ' + firstFormatArray[1]
    }
  },
  formatDateToToday: (value) => {
    if (typeof value === 'string') {
      (value).replace(/-/g, '/')
      const date = new Date(value)
      const today = new Date()
      const num = (today - date) / (1000 * 3600 * 24) - 1
      const days = parseInt(Math.ceil(num))
      return days
    }
  },
  changeToPercent(value) {
    if (/(^[1-9]\d*(\.\d{1,2})?$)|(^0(\.\d{1,2})?$)/.test(value)) {
      return
    }
    const result = ((value * 100).toFixed(2)).toString()
    const index = result.indexOf('.')
    if (index === -1 || result.substr(index + 1).length <= 2) {
      return result + '%'
    }
    return result.substr(0, index + 3) + '%'
  },
  compact: (val, index = 0) => {
    return _.compact(val)[index]
  }
}

Object.defineProperty(filters, 'install', {
  value: (Vue, Option) => {
    Object.keys(filters).forEach(key => {
      Vue.filter(key, filters[key])
    })
    Vue.$filters = filters
    Vue.prototype.$filters = filters
  }
})

export default filters
