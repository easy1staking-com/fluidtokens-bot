package com.fluidtokens.nft.borrow.service;

import co.nstant.in.cbor.model.ByteString;
import com.bloxbean.cardano.client.address.Credential;
import com.bloxbean.cardano.client.exception.CborDeserializationException;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.plutus.spec.PlutusV2Script;
import com.bloxbean.cardano.client.util.HexUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FluidtokensRentContractService {

    private static final String FLUIDTOKENS_SCRIPTS_BYTES = "5919bf0100003232323232323232322223232323232533300b32323232323232323232323253330173370e90010008991919191919299980e99b874800801854ccc074cc004c040c06c060c040c06c0684cc00cc040c06c060dd69802980d80d0a50132323232323232323232323253330293370e90030090991919191919191919191919299981a99b87375a602e60660646eb4c044c0cc09454ccc0d4c0480c854ccc0d4cdc4a400000a2a66606a66e2120000011533303533019302830330303028303303215333035021133333333222222223232533303f3370e9000181f0008991919299982119b8748010c1040044c8c8c8c8c8c8c94ccc124ccc124cdc79bae303d30470464881004a094454ccc124cdd79ba6302337566076608e0126e98ccc084dd7181e98238231bae303e3047046375a6056608e08c2002294054ccc124cdd7981d98238049ba6333021375c607a608e08c6eb8c0f8c11c118dd69815982382308008a5053330483375e6076608c0100022a66609066e25200000f153330483371290010060a99982419baf0030051337106eb4c0c0c11800cdd6981498230018a5014a0294052819ba548000cc12c004cc12cc0e4c114c0b0c1141112f5c066e9520023304a375201697ae03374a900019824981c1821821198249ba900e33049375201a660926ea0030cc124c0e4c10c108cc124c0e8c10c108cc124c09cc10c108cc124c0b0c10c108cc124c0a8c10c108cc124c0b4c10c108cc124dd400799824981018218211982498129821821198249ba800933049302330430424bd70181e000982400098200008b180e981f8009822800981e8008b19815800802198171bac3019303b0082323253330403370e9001000899b8f375c608a607c00400a2940c0f8004c0c4c0f0c0c4c0f0004c0a0c0cc0c002c02401c01400cdd7181418198128008a5014a029405280a5014a06eb4c0e4004c0e4008dd6981b800981b8011bad30350013035002375c606600260660046eb8c0c4004c0c4008dd69817800981381289919299981599b87480100504c8c8c8c8c8c8c8c94ccc0ccc0400c054ccc0cccdc39bad30153031030375a601e60620462a66606666e2400cdd6980798188118a99981999b884800000c54ccc0cccdc49bad30123031030007153330333370e66e1801cdd698089818818240002a66606603e2a6660666666666646444444446464a66607c66e1d2000303d001132323253330413370e90021820000899191919299982299b8748008c1100044c8c8c94ccc120cdc3a4004608e00226464646464646464a6660a066e21200001413253330513370e900018280008991919299982a19b8748010c14c0044c8c8c8c8c8c94ccc168ccc168cdc79bae304e30580254881004a094454ccc168cdd79ba630343756609860b00366e9804054ccc168cdd79ba630343756609860b00106e98cc09c03cc09804054ccc168004402c5280a5014a02a6660b466ebcc130c16006cdd30080a99982d19baf304c3058008374c6604e01e604c0202a6660b4002201629405280a5053330593375e002008266ebcc130c15c01c00852819ba548000cc170c12cc15808ccc170c128c15808ccc170c0d0c15808ccc170c0ecc15808ccc170c130c15808ccc170c134c15808ccc170dd400e1982e181f982b0119982e1825982b0119982e1820182b0119982e181c982b0119982e1819982b0119982e181c182b0119982e181b982b0119982e181b182b011a5eb80cdd2a4000660b6016660b6609260aa609460aa04497ae0304e001305a001305200116302f30510013057001304f001163303d37586058609c03466e00cdc100ba400890010a99982819982819b8f375c6088609c0369101004a094454ccc140cdd79ba6302a37566084609c0226e9801440045280a99982819baf3042304e011374c00a200229414ccc13ccdd7982118268080010a99982799b88375a606e609a0340122a66609e66ebc00403454ccc13ccdc499b8100600948202eadc0c4cdc41bad3037304d001375a6060609a00229405280a5014a066e952000330523041304c019330523040304c01933052302a304c019330523031304c019330523042304c019330523043304c019330523750028660a4606a6098032660a402e660a46ea0cdc000299b8248203c2c948058cc148c0bcc130064cc148c0a4c130064cc148c0b8c130064cc148c0b4c130064cc148c0b0c1300652f5c066e9520003305100133051303f304b0164bd7019ba548008cc140dd4808a5eb80ccc08cdd7181f982480b1bae30403049016375a605a609202c6660446eb8c0f8c120054dd7181f982400a8081bad304e001304600116303a304530393045302e3045011375a609600260860022c606e6084606e60846056608401c6076002608e002607e0022c6038607c002608800260780022c660546eb0c064c0ec01ccdc10022400866e04dd6980f181d0038011191980080080111299981d0008a5eb7bdb1804c8c8c8cc0f8cdd81ba9001374c64646600200200444a666080002297adef6c6013233042337606ea4dd7181f8009ba83370290001bad30400013300300330440023042001375660780066600a00a0046eb8c0e8008c0f8008c0f000488c8ccc00400400c0088894ccc0ec00840044c8ccc010010c0fc00ccccc8888c8cc00400401c894ccc1080044cc10ccdd81ba9006374c00a97adef6c6013232323253330433375e6600e014004980103d8798000133047337606ea4028dd30048028a99982199b8f00a0021323253330453370e900000089982499bb037520186094608600400a200a608600266601001401200226608e66ec0dd48011ba600133006006003375660880066eb8c108008c118008c110004dd7181d0009bab303b00122232533303d533304000114a22940530103d87a800013374a9000198209ba60014bd701919980080080180111129998210010800899199802002182300199999111191980080080391299982480089982519bb0375200c6ea00152f5bded8c0264646464a66609466ebccc01c028009300103d879800013304e337606ea4028dd40048028a99982519b8f00a00213232533304c3370e900000089982819bb0375201860a2609400400a200a609400266601001401200226609c66ec0dd48011ba800133006006003375a60960066eb8c124008c134008c12c004dd718208009bad30420012223253330443370e00290000a6103d87a800013374a9000198241ba80014bd7019b800020010193044002012303d0020303026303102e001007005003375c604c60620462a6660666666666666012604c606205c6eb8c094c0c40c0dd718079818818181318188181bad30163031030007005003001375a60346062060266666666666014604c606205c6eb8c094c0c40c0dd718079818818180718188181bad30163031030007375a6026606206000a006603060620606eb4c068c0c40c05280a5014a029405280a5014a02940528181b800981b8011bad30350013035002375a606600260660046eb4c0c4004c0a409c4c94ccc0b0cdc3a400002a264646464a666060601a05a2a66606066e1cdd6980918170169bad300c302e02015333030337126eb4c03cc0b80b400c54ccc0c0cdc399b86003375a601c605c05a90000a9998181980a18119817015980a98170168a99981800e0a9998181998181980b181198170159bad3018302e02d4a094454ccc0c0cccccc0140b4c08cc0b80ac00c004dd718119817010180a98170168a9998181999999999803181198170159bae3022302e02d375c6018605c05a6046605c05a6eb4c04cc0b80b400c004dd698061817010180a98170169bad3017302e02d1333333333330073023302e02b375c6044605c05a6eb8c030c0b80b4c02cc0b80b4dd6980998170168019bad3010302e02d001375a6018605c040602a605c05a6eb4c05cc0b80b45280a5014a029405280a5014a029405281bad30340013034002375a606400260540502a66605866e1d200801513232533302e3370e6eb4c040c0b00acdd69805181600f0a99981718058158a99981700d0a9998171980a181098160149bad3016302c02b1533302e33333300302b3021302c02948000004dd71810981600f18109816015899981719baf3021302c02b3013302c02b4a09445280a5014a029405281bad3032001302a02813232533302e3370e6eb4c040c0b00acdd69805181600f0a99981718058158a99981700d0a9998171998171980a181098160149bad3016302c02b4a094454ccc0b8cc048c084c0b00a4c04cc0b00ac54ccc0b8cccccc00c0acc084c0b00a52000001375c6042605803c60266058056266605c66ebcc084c0b00acc04cc0b00ad2825114a029405280a5014a02940dd69819000981501411111119299981919b8748000c0c40044c8c8c94ccc0d4cdc3a40086068002264646464a66607266e1d200230380011323232533303c3370e9001181d8008991919191919299982119982119b8f375c606c608002e911004a094454ccc108cdd79ba6301c37566068608001e6e98ccc068dd7181b182000b9bae30373040017375a6048608002e2002294054ccc108cdd7981a18200079ba633301a375c606c608002e6eb8c0dcc10005cdd69812182000b88008a5053330413375e6068607e01c0022a66608266ebc00c02c54ccc104cdc499b8100400748202eadc0c4cdc41bad3029303f003375a6044607e00629405280a503374a900019822000998221819181f00825eb80cdd2a4004660866ea40412f5c066e952000330423031303c013330423030303c01333042301a303c013330423021303c013330423032303c013330423033303c013330423020303c013330423025303c0133304200e33042375066e00dd69813181e00999b8248203c2c948044cc108c07cc0f004ccc108c064c0f004ccc108c078c0f004ccc108c074c0f004ccc108c070c0f004d2f5c06eb4c108004c0e800458c0b8c0e4c0b4c0e4c088c0e403cdd6981f800981b8008b1815981b1815981b180f981b0061817800981d80098198008b18081819000981c00098180008b1980f1bac300d302f005003222222222253330343370e00c90000a511323253330363370e9000181a80089919299981c19981c19b8f00d4881004a094454ccc0e0cdd79ba6301237566054606c0026e98ccc040034030cdc119b823370600e00a014012266ebcc0acc0d800402c5280a99981c19baf302a3036001374c66602001a01866e08cdc119b8300700500a00913375e6056606c0020162940c0f0004c0d000458cc088004cdc1002a40086604a6eb0c040c0c802894ccc0d4ccc0d4cdd7981418199814181980098141819801a504a2266ebcc044c0cc00530103d879800014a044444444444a66606866e1c015200014a226464a66606c66e1d200030350011323253330383330383371e01c9101004a094454ccc0e0cdd79ba6301237566054606c0026e98ccc040038034cdc199b833370466e08cdc1005805003804a41a01e00a266ebcc0acc0d80040305280a99981c19baf302a3036001374c66602001c01a66e0ccdc199b823370466e0802c02801c02520d00f00513375e6056606c0020182940c0f0004c0d000458cc088004cdc019b82005480112002330253758602060640164a66606a66606a66ebcc0a0c0ccc0a0c0cc004c0a0c0cc00d2825113375e60226066002980103d879800014a0444a66605666e1c005200014bd6f7b630099911919800800a5eb7bdb180894ccc0c80044cc0cccdd81ba9007374c00897adef6c6013232323253330333375e6600e01600498103d8798000133037337606ea402cdd30040028a99981999b8f00b002133037337606ea402cdd300400189981b99bb037520046e98004cc01801800cdd5981a0019bae30320023036002303400132330010014bd6f7b63011299981800089981899bb037520086ea000d2f5bded8c0264646464a66606266ebccc02802000930103d8798000133035337606ea4020dd40038028a99981899b8f008002133035337606ea4020dd400380189981a99bb037520046ea0004cc01801800cdd698190019bae303000230340023032001004225333029337200040022980103d8798000153330293371e0040022980103d87a800014c103d87b80002323300100100222533302c00114bd6f7b630099191919299981699b8f4881000021003133031337606ea4008dd3000998030030019bab302e003375c60580046060004605c002460566058605860586058605860586058605860586058605800246054605660560024a66604866e252000375a600e60440022a66604866e212000375a600c60440022a66604866e212000375a601660440022a66604866e252000375a601860440022a66604866e252000375a600a60440022a66604866e252000375a600860440022a66604866e212000375a60066044002266e212000375a6004604400229405280a5014a029405280a5023028302930293029302930293029302930293029302930293029302930290012302730283028302830283028302830283028302830283028302830280012302630273027302730273027302730273027302730273027302700123025302630263026302630263026302630263026302600123024302530253025302530253025001230233024302430240012232533301f3370e9000180f0008999119198008008019129998130008a501323253330253371e00400a29444cc010010004c0a8008dd718140009bac3004301d003375c6048603a0022c602260380024604260446044604460446044604460446044002446464a66603c66e1d20020011337100066eb4c08cc070008528180e0009807980d1807980d1801980d0011180f98101810181018101810181018100009180f180f980f980f980f980f980f980f980f980f800980b0098a503015001300730130103332223253330183370e90010008a5113332223232533301d3370e9000180e000899baf3010301b3022301b00100316330090010033300c00723232533301e3370e9001000899b8f375c6046603800400c2940c070004c03cc068c03cc068c038c068004dd71805980b0010019805180b001180b0099bac300730123007301200f0030043001001222533301800214c103d87a80001323253330173370e0069000099ba548000cc06c0092f5c0266600a00a00266e0400d2002301c003301a0023253330123370e90010008a40002a66602466e1d20000011375a602e6030602001c2a66602466e1d20040011375a602e6030602001c2a66602466e1d20060011375a602e6030603060306030602001c2a66602466e1d20080011375a602e602001c26eb4c05cc040038c040034cccc88894ccc050cdc3a40046026008264646464a66603066e1d20003017001132323232533301c3370e9001000899ba548000cc080c084c068008cc08001ccc080dd419991119199119299981199b874800800440084dd698141810801981080119299981099b87480080045300103d87a8000132323300100100222533302700114c103d87a800013232323253330283371e014004266e9520003302c375000297ae0133006006003375a60520066eb8c09c008c0ac008c0a4004dd59813180f801180f800a4000646600200200844a6660480022980103d87a800013232323253330253371e010004266e95200033029374c00297ae01330060060033756604c0066eb8c090008c0a0008c098004dd59807180d1807180d001805004a5eb8058c068004c034c060c034c060c030c060004c078004c058004594ccc068004530103d87a800013374a90001980d980e000a5eb80cc01c0148cdd79805180a800801180d00098090020b180118070059bac3003300e3003300e00b375c6008601c01a6eb8c014c03803488c8cc00400400c894ccc05800452f5c026464a66602a600a004266032004660080080022660080080026034004603000246028602a00246026002460246026602660266026002460226024602460246024602400229309b2b19299980599b87480000044c8c8c8c94ccc048c05400852616375a602600260260046eb4c044004c02401c54ccc02ccdc3a40040022a66601c601200e2930b0a99980599b87480100044c8c8c8c8c8c8c8c94ccc058c0640084c926300c0011630170013017002375a602a002602a0046eb4c04c004c04c008dd6980880098048038a99980599b87480180044c8c8c8c8c8c8c8c8c8c8c8c94ccc068c07400852616375a603600260360046eb4c064004c064008dd6980b800980b8011bae30150013015002375c602600260260046eb4c044004c02401c54ccc02ccdc3a401000226464a66602060260042930b1bad301100130090071533300b3370e900500089919299980818098010a4c2c6eb4c044004c02401c58c024018c0040188c94ccc028cdc3a40000022646464646464646464646464646464646464646464646464646464646464a666056605c00426464649318118049811007181080e8b1bad302c001302c002375a605400260540046eb4c0a0004c0a0008c098004c098008dd6981200098120011bad3022001302200230200013020002375a603c002603c0046eb4c070004c070008dd7180d000980d0011bae30180013018002375a602c002602c0046eb8c050004c050008dd718090009809001180800098040010b1804000919299980499b87480000044c8c8c8c94ccc040c04c0084c8c92632533300f3370e900000089919299980a180b80109924c64a66602466e1d2000001132325333017301a002132498c03800458c060004c04000854ccc048cdc3a40040022646464646464a666036603c0042930b1bad301c001301c002375a603400260340046eb4c060004c04000858c04000458c054004c03400c54ccc03ccdc3a40040022a666024601a0062930b0b180680118038018b18088009808801180780098038010b1803800919299980419b87480000044c8c94ccc034c04000852616375c601c002600c0042a66601066e1d200200113232533300d3010002149858dd7180700098030010b1803000918029baa001230033754002ae6955ceaab9e5573eae815d0aba21";

    private final Credential paymentCredentials;

    public FluidtokensRentContractService() {
        PlutusV2Script poolScript;
        try {
            poolScript = PlutusV2Script.deserialize(new ByteString(HexUtil.decodeHexString(FLUIDTOKENS_SCRIPTS_BYTES)));
            paymentCredentials = Credential.fromScript(poolScript.getScriptHash());
        } catch (CborDeserializationException | CborSerializationException e) {
            throw new RuntimeException(e);
        }
    }

    public Credential getPaymentCredentials() {
        return paymentCredentials;
    }

    public String getPaymentCredentialsHex() {
        return HexUtil.encodeHexString(getPaymentCredentials().getBytes());
    }

}