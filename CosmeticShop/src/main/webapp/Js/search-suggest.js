// Lightweight debounce
function debounce(fn, delay) {
    let t;
    return function () {
        const ctx = this;
        const args = arguments;
        clearTimeout(t);
        t = setTimeout(function () { fn.apply(ctx, args); }, delay);
    };
}

(function () {
    const input = document.getElementById('globalSearchInput');
    const dropdown = document.getElementById('searchSuggestions');
    const searchBtn = document.getElementById('globalSearchBtn');
    if (!input || !dropdown) return;

    function getContextPath() {
        if (window.APP_CTX) return window.APP_CTX;
        var el = document.querySelector('body');
        if (el && el.getAttribute('data-context-path')) {
            return el.getAttribute('data-context-path');
        }
        var path = window.location.pathname;
        var index = path.indexOf('/', 1);
        return index > 0 ? path.substring(0, index) : '';
    }
    const contextPath = getContextPath();

    function hide() {
        dropdown.style.display = 'none';
        dropdown.innerHTML = '';
    }

    function render(items) {
        if (!items || items.length === 0) {
            hide();
            return;
        }
        var html = items.map(function (item) {
            var img = (item.imageUrl && item.imageUrl.length) ? (contextPath + item.imageUrl) : (contextPath + '/IMG/hinhnen-placeholder.png');
            var price = new Intl.NumberFormat('vi-VN', { maximumFractionDigits: 0 }).format(item.price);
            var originalPrice = new Intl.NumberFormat('vi-VN', { maximumFractionDigits: 0 }).format(item.originalPrice || item.price);
            var hasDiscount = !!item.hasDiscount && item.originalPrice && item.originalPrice > item.price;
            var priceHtml = '<div class="suggest-price"><span class="suggest-price-current">' + price + ' VNĐ</span>';
            if (hasDiscount) {
                priceHtml += '<span class="suggest-price-original">' + originalPrice + ' VNĐ</span>';
            }
            priceHtml += '</div>';
            return '' +
                '<a class="suggest-item" href="' + item.url + '">' +
                '  <img class="suggest-img" src="' + img + '" alt="">' +
                '  <div class="suggest-meta">' +
                '    <div class="suggest-name">' + item.name + '</div>' +
                '    ' + priceHtml +
                '  </div>' +
                '</a>';
        }).join('');
        dropdown.innerHTML = html;
        dropdown.style.display = 'block';
    }

    var fetchSuggest = debounce(function (value) {
        if (!value || value.trim().length === 0) {
            hide();
            return;
        }
        var url = contextPath + '/search-suggestions?q=' + encodeURIComponent(value.trim()) + '&limit=8';
        fetch(url, { headers: { 'Accept': 'application/json' } })
            .then(function (r) { return r.ok ? r.json() : []; })
            .then(function (data) {
                render(Array.isArray(data) ? data : []);
            })
            .catch(function () {
                hide();
            });
    }, 180);

    input.addEventListener('input', function (e) {
        fetchSuggest(e.target.value);
    });
    input.addEventListener('focus', function (e) {
        if (dropdown.innerHTML && dropdown.innerHTML.length > 0) {
            dropdown.style.display = 'block';
        }
    });
    input.addEventListener('keydown', function (e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            var val = input.value ? input.value.trim() : '';
            if (val.length > 0) {
                window.location.href = contextPath + '/products?search=' + encodeURIComponent(val);
            }
        }
    });
    if (searchBtn) {
        searchBtn.addEventListener('click', function (e) {
            e.preventDefault();
            var val = input.value ? input.value.trim() : '';
            if (val.length > 0) {
                window.location.href = contextPath + '/products?search=' + encodeURIComponent(val);
            }
        });
    }
    document.addEventListener('click', function (e) {
        if (!dropdown.contains(e.target) && e.target !== input) {
            hide();
        }
    });
})();


