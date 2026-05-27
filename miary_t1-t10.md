## Wzory na miary jakości od $T_{1}$ do $T_{10}$

- **$T_{1}$ (Interval-valued degree of truth):** W podanym fragmencie nie zaprezentowano pełnego równania, a jedynie formę przedziałową miary zapisaną jako $T_{1}=[\underline{t}_{1},\overline{t}_{1}]$.

- **$T_{2}$ (Interval-valued degree of imprecision):**
  $T_{2}=[\underline{t}_{2},\overline{t}_{2}]=\left[1-\left(\prod_{j=1}^{n}\overline{in}(S_{j})\right)^{1/n},1-\left(\prod_{j=1}^{n}\underline{in}(S_{j})\right)^{1/n}\right]$

- **$T_{3}$ (Interval-valued degree of covering):**
  - Dla kwalifikatora typu 1 (forma podstawowa):
    $T_{3}=[\underline{t}_{3},\overline{t}_{3}]=\frac{\left[\sum_{i=1}^{m}\underline{t}_{i},\sum_{i=1}^{m}\overline{t}_{i}\right]}{\sum_{i=1}^{m}h_{i}}$
  - Dla kwalifikatora typu 1 (forma oparta na nośnikach):
    $T_{3}=[\underline{t}_{3},\overline{t}_{3}]=\frac{[|\underline{supp}(S\cap W\cap\mathcal{D})|,|\overline{supp}(S\cap W\cap\mathcal{D})|]}{|supp(W\cap\mathcal{D})|}$
  - Dla kwalifikatora przedziałowego:
    $T_{3}=[\underline{t}_{3},\overline{t}_{3}]=\frac{[|\underline{supp}(S\cap W\cap\mathcal{D})|,|\overline{supp}(S\cap W\cap\mathcal{D})|]}{[|\underline{supp}(W\cap\mathcal{D})|,|\overline{supp}(W\cap\mathcal{D})|]}$

- **$T_{4}$ (Interval-valued degree of appropriateness):**
  $T_{4}=[\underline{t}_{4},\overline{t}_{4}]=\left[\left|\prod_{j=1}^{n}\underline{r}_{j}-\overline{t}_{3}\right|,\left|\prod_{j=1}^{n}\overline{r}_{j}-\underline{t}_{3}\right|\right]$

- **$T_{5}$ (Length of an interval-valued summary):** Miara ta nie jest wyrażana jako typowy przedział. W obliczeniach można jej użyć jako przedziału zdegenerowanego $[t_{5},t_{5}]$, w którym $\underline{t}_{5}=\overline{t}_{5}$.

- **$T_{6}$ (Interval-valued degree of quantifier imprecision):**
  - Dla kwantyfikatora absolutnego:
    $T_{6}=[\underline{t}_{6},\overline{t}_{6}]=\left[1-\frac{|\overline{supp}(Q)|}{|\mathcal{X}_{Q}|},1-\frac{|\underline{supp}(Q)|}{|\mathcal{X}_{Q}|}\right]$
  - Dla kwantyfikatora relatywnego ($|\mathcal{X}_{Q}|=1$):
    $T_{6}=[\underline{t}_{6},\overline{t}_{6}]=[1-|\overline{supp}(Q)|,1-|\underline{supp}(Q)|]$

- **$T_{7}$ (Interval-valued degree of quantifier cardinality):**
  - Dla kwantyfikatora absolutnego:
    $T_{7}=[\underline{t}_{7},\overline{t}_{7}]=[1-\overline{rc}(Q),1-\underline{rc}(Q)]$
  - Dla kwantyfikatora relatywnego ($|\mathcal{X}_{Q}|=1$):
    $T_{7}=[\underline{t}_{7},\overline{t}_{7}]=[1-|\overline{Q}|,1-|\underline{Q}|]$

- **$T_{8}$ (Interval-valued degree of summarizer cardinality):**
  $T_{8}=[\underline{t}_{8},\overline{t}_{8}]=\left[1-\left(\prod_{j=1}^{n}\overline{rc}(S_{j})\right)^{1/n},1-\left(\prod_{j=1}^{n}\underline{rc}(S_{j})\right)^{1/n}\right]$

- **$T_{9}$ (Interval-valued degree of qualifier imprecision):**
  - Gdy kwalifikator jest reprezentowany przez jeden przedziałowy zbiór rozmyty:
    $T_{9}=[\underline{t}_{9},\overline{t}_{9}]=[1-\overline{in}(W),1-\underline{in}(W)]$
  - Gdy kwalifikator jest reprezentowany przez dwa lub więcej przedziałowych zbiorów rozmytych:
    $T_{9}=[\underline{t}_{9},\overline{t}_{9}]=\left[1-\left(\prod_{j=1}^{x}\overline{in}(W_{g_{j}})\right)^{1/x},1-\left(\prod_{j=1}^{x}\underline{in}(W_{g_{j}})\right)^{1/x}\right]$

- **$T_{10}$ (Interval-valued degree of qualifier cardinality):**
  - Gdy kwalifikator jest reprezentowany przez jeden przedziałowy zbiór rozmyty:
    $T_{10}=[\underline{t}_{10},\overline{t}_{10}]=[1-\overline{rc}(W),1-\underline{rc}(W)]$
  - Gdy kwalifikator jest reprezentowany przez dwa lub więcej przedziałowych zbiorów rozmytych:
    $T_{10}=[\underline{t}_{10},\overline{t}_{10}]=\left[1-\left(\prod_{j=1}^{x}\overline{rc}(W_{g_{j}})\right)^{1/x},1-\left(\prod_{j=1}^{x}\underline{rc}(W_{g_{j}})\right)^{1/x}\right]$

---

## Miara optymalności podsumowania ($T$)

$T=[\underline{t},\overline{t}]=\left[\sum_{i=1}^{10}w_{i}\cdot\underline{t}_{i},\sum_{i=1}^{10}w_{i}\cdot\overline{t}_{i}\right]$
